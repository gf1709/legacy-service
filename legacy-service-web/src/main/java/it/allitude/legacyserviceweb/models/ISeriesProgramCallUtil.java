package it.allitude.legacyserviceweb.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400PackedDecimal;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400Time;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.PackedDecimalFieldDescription;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.ibm.as400.access.ZonedDecimalFieldDescription;

import io.jsonwebtoken.io.IOException;
import it.allitude.legacyserviceweb.DTOs.DSPOBJDRequestDTO;
import it.allitude.legacyserviceweb.DTOs.DSPOBJDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.FFDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.ProgramCallRequestDTO;
import it.allitude.legacyserviceweb.DTOs.ProgramCallResponseDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.dsdef.ZZdPRM;

@Component
public class ISeriesProgramCallUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConnectionService _connectionService;
    ISeriesObjectUtil _objectUtil;
    public ISeriesProgramCallUtil(ConnectionService iseriesConnectionService) {

        this._connectionService = iseriesConnectionService;
        _objectUtil = new ISeriesObjectUtil(this._connectionService);
    }

    static final String g_uploadDirectory = "run_time_resources/static/call_history";

    String getCallHistoryFileName() {
        String res = String.format("%s_%s.txt", JSession.getCurrentSession().getUser().toUpperCase(),
                JSession.getCurrentSession().getTerminal().toUpperCase());
        return res;
    }

    final Integer HISTORY_CALL_SIZE = 200;

    void saveCall(ProgramCallRequestDTO aCall) throws java.io.IOException, ClassNotFoundException {
        Path uploadPath = Paths.get(g_uploadDirectory);
        Path filePath = uploadPath.resolve(getCallHistoryFileName());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        boolean historyContainsCall = false;
        ArrayList<ProgramCallRequestDTO> historyCalls = new ArrayList<ProgramCallRequestDTO>();
        try {
            FileInputStream fis = new FileInputStream(filePath.toAbsolutePath().toString());
            ObjectInputStream ois = new ObjectInputStream(fis);
            historyCalls = (ArrayList<ProgramCallRequestDTO>) ois.readObject();

            ois.close();
            fis.close();
        } catch (Exception e) {
        }

        for (ProgramCallRequestDTO c : historyCalls) {
            if (c.equals(aCall)) {
                historyContainsCall = true;
                c.setWhen(LocalDateTime.now().format(getFormatter()));
            }
        }

        historyCalls.sort((o1, o2) -> o1.compareTo(o2));
        Collections.reverse(historyCalls);

        if (historyCalls.size() > HISTORY_CALL_SIZE) {
            historyCalls = (ArrayList<ProgramCallRequestDTO>) historyCalls.subList(0, HISTORY_CALL_SIZE);
        }
        // Se gia' esiste non l'aggiungo
        if (!historyContainsCall) {
            historyCalls.add(aCall);
        }

        historyCalls.sort((o1, o2) -> o1.compareTo(o2));
        Collections.reverse(historyCalls);

        FileOutputStream fos = new FileOutputStream(filePath.toAbsolutePath().toString());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(historyCalls);
        oos.close();
        fos.close();
    }

    public void saveHistoryCall(ArrayList<ProgramCallRequestDTO> calls)
            throws java.io.IOException, ClassNotFoundException {

        Path uploadPath = Paths.get(g_uploadDirectory);
        Path filePath = uploadPath.resolve(getCallHistoryFileName());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        ArrayList<ProgramCallRequestDTO> historyCalls = calls;

        historyCalls.sort((o1, o2) -> o1.compareTo(o2));
        Collections.reverse(historyCalls);

        if (historyCalls.size() > HISTORY_CALL_SIZE) {
            historyCalls = (ArrayList<ProgramCallRequestDTO>) historyCalls.subList(0, HISTORY_CALL_SIZE);
        }

        FileOutputStream fos = new FileOutputStream(filePath.toAbsolutePath().toString());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(historyCalls);
        oos.close();
        fos.close();
    }

    public ArrayList<ProgramCallRequestDTO> retrieveHistoryCall() {
        ArrayList<ProgramCallRequestDTO> historyCalls = new ArrayList<ProgramCallRequestDTO>();
        Path uploadPath = Paths.get(g_uploadDirectory);
        Path filePath = uploadPath.resolve(getCallHistoryFileName());
        try {
            FileInputStream fis = new FileInputStream(filePath.toAbsolutePath().toString());
            ObjectInputStream ois = new ObjectInputStream(fis);
            historyCalls = (ArrayList<ProgramCallRequestDTO>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
        }
        return historyCalls;
    }

    public ProgramCallResponseDTO callProgram(ProgramCallRequestDTO aCall) throws Exception {

        ProgramCallResponseDTO res = new ProgramCallResponseDTO();
        res.setProgram(aCall.getProgram());
        res.setCommand(aCall.getCommand());
        res.setDsin(aCall.getDsin());
        res.setDsout(aCall.getDsout());
        res.setFlagIO(aCall.getFlagIO());
        res.setType(aCall.getType());

        ZZdPRM prmRecordFormat = new ZZdPRM();
        Record prmIn = prmRecordFormat.getNewRecord();
        prmIn.setField(ZZdPRM.ZZPFPC, " ");
        prmIn.setField(ZZdPRM.ZZPCMD, aCall.getCommand() != null ? aCall.getCommand() : "        ");
        prmIn.setField(ZZdPRM.ZZPFIO, aCall.getFlagIO() != null ? aCall.getFlagIO() : " ");
        prmIn.setField(ZZdPRM.ZZPDSI, aCall.getDsin() != null ? aCall.getDsin() : "        ");
        prmIn.setField(ZZdPRM.ZZPDSO, aCall.getDsout() != null ? aCall.getDsout() : "        ");
        prmIn.setField(ZZdPRM.ZZPCID, aCall.getCid() != null ? aCall.getCid() : "        ");
        prmIn.setField(ZZdPRM.ZZPRTC, "  ");

        RecordFormat datInRecFormat = new RecordFormat();
        for (ISeriesFieldValue fld : aCall.getValues()) {
            if (fld.getType().equals("A")
                    || fld.getType().equals("L")
                    || fld.getType().equals("T")) {
                datInRecFormat.addFieldDescription(
                        new CharacterFieldDescription(new AS400Text(fld.getLength()), fld.getName()));
            } else if (fld.getType().equals("S")) {
                datInRecFormat.addFieldDescription(new ZonedDecimalFieldDescription(
                        new AS400ZonedDecimal(fld.getLength(), fld.getScale()), fld.getName()));
            } else if (fld.getType().equals("P")) {
                datInRecFormat.addFieldDescription(new PackedDecimalFieldDescription(
                        new AS400PackedDecimal(fld.getLength(), fld.getScale()), fld.getName()));
            }
        }
        // datInRecFormat.addFieldDescription(new CharacterFieldDescription(new
        // AS400Text(1), "PIPE"));

        Record datInRec = datInRecFormat.getNewRecord();
        for (ISeriesFieldValue fld : aCall.getValues()) {
            if (fld.getType().equals("A")
                    || fld.getType().equals("L")
                    || fld.getType().equals("T")) {
                datInRec.setField(fld.getName(), fld.getValue());
            } else if (fld.getType().equals("S")) {
                {
                    BigDecimal bd = new BigDecimal(fld.getValue());
                    datInRec.setField(fld.getName(), bd);
                }
            } else if (fld.getType().equals("P")) {
                BigDecimal bd = new BigDecimal(fld.getValue());
                datInRec.setField(fld.getName(), bd);
            }
        }
        // datInRec.setField("PIPE", "|");
        byte[] datInBytes = new byte[1];
        datInBytes[0] = ' ';
        if (datInRec != null) {
            datInBytes = datInRec.getContents();
        }
        ProgramParameter[] parmList = new ProgramParameter[2];
        parmList[0] = new ProgramParameter(prmIn.getContents(), 36);
        parmList[1] = new ProgramParameter(datInBytes, 3840);

        ProgramCall pgm = new ProgramCall(
                _connectionService.getAS400Connection(),
                QSYSObjectPathName.toPath("*LIBL", aCall.getProgram(), "PGM"), parmList);
        try {
            if (pgm.run() != true) {
                AS400Message[] messageList = pgm.getMessageList(); // Errore !!
                logger.info("messageList.length is " + messageList.length);
            } else {
                String dsOutName = aCall.getDsout();

                RecordFormat datOutRecFormat = getRecordFormat(dsOutName);                
                FFDResponseDTO ffd = _objectUtil.getFFD("", dsOutName);
                byte[] prmOutBytes = parmList[0].getOutputData();
                Record prmOut = prmRecordFormat.getNewRecord(prmOutBytes);
                res.setResult(prmOut.getField(ZZdPRM.ZZPRTC).toString());
                String rtc = res.getResult();

                if (rtc != null && rtc.length() > 1 && rtc.substring(0, 1).equals("0")) {
                    byte[] datOutBytes = parmList[1].getOutputData();
                    Record datOutRec = datOutRecFormat.getNewRecord(datOutBytes);
                    for (ISeriesFieldDescription fld : ffd.getFields()) {
                        ISeriesFieldValue newVal = new ISeriesFieldValue();
                        newVal.setName(fld.getFieldName());
                        newVal.setLength(fld.getFieldLength());
                        newVal.setScale(fld.getFieldScale());
                        newVal.setType(fld.getFieldType());
                        newVal.setDescription(fld.getFieldDescription());
                        newVal.setValue(datOutRec.getField(fld.getFieldName()).toString());
                        res.getValues().add(newVal);
                    }
                }
                logger.info("Program name called.............: " + aCall.getProgram());
            }
        } catch (AS400SecurityException e) {
            res.setResult("91");
        } catch (ErrorCompletingRequestException e) {
            res.setResult("92");
        } catch (IOException e) {
            res.setResult("93");
        } catch (InterruptedException e) {
            res.setResult("94");
        } catch (ObjectDoesNotExistException e) {
            res.setResult("90");
        }

        aCall.setWhen(LocalDateTime.now().format(getFormatter()));
        saveCall(aCall);
        return res;
    }

    private DateTimeFormatter getFormatter() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return format;
    }

    public ArrayList<ProgramCallResponseDTO> showISYOutput(String aVal) throws Exception {
        ArrayList<ProgramCallResponseDTO> res = new ArrayList<>();
        byte[] aRequest = getDigits(aVal);
        // logger.info("aVal is: " + aVal);
        int bI = 0;
        CharsetDecoder ebcdic2asciiDecoder = getEBCDIC2ASCIIDecoder();

        while (bI < aRequest.length) {
            int bMessageType = aRequest[bI];
            bI += 1;
            if (bMessageType != 3 && bMessageType != 4) {
                logger.error("Tipo messaggio non valido per showISYOutput: " + bMessageType);
                break;
            }
            int bMessageID = (256 * 256 * 256 * Byte.toUnsignedInt(aRequest[bI])) + (256 * 256 * Byte.toUnsignedInt(aRequest[bI + 1])) + (256 * Byte.toUnsignedInt(aRequest[bI + 2])) + Byte.toUnsignedInt(aRequest[bI + 3]);
            bI += 4;
            if (bMessageID < 0) {
                logger.error("Messagge ID non valido per showISYOutput: " + bMessageID);
                break;
            }
            int bMessageLen = (256 * 256 * 256 * Byte.toUnsignedInt(aRequest[bI])) + (256 * 256 * Byte.toUnsignedInt(aRequest[bI + 1])) + (256 * Byte.toUnsignedInt(aRequest[bI + 2])) + Byte.toUnsignedInt(aRequest[bI + 3]);
            bI += 4;
           if (bMessageLen < 0) {
                logger.error("Messagge Len non valida per showISYOutput: " + bMessageLen);
                break;
            }
            // Leggo l'header
            int times = (256 * Byte.toUnsignedInt(aRequest[bI]) + Byte.toUnsignedInt(aRequest[bI + 1]));
            bI += 2;
            String returnCode = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 2)).toString().trim();
            bI += 2;
            String dso = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;
            String cid = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;

            logger.debug("====================================================================>\n");
            logger.debug("messageType : " + bMessageType);
            logger.debug("messageID   : " + bMessageID);
            logger.debug("messageLen  : " + bMessageLen);
            logger.debug("times       : " + times);
            logger.debug("returnCode  : " + returnCode);
            logger.debug("dso         : " + dso);
            logger.debug("cid         : " + cid);
            logger.debug("====================================================================>\n");
            ProgramCallResponseDTO pgmCallRes = new ProgramCallResponseDTO();
            if (bMessageType == 3) {
                pgmCallRes.setDsout(dso);
                pgmCallRes.setResult(returnCode);
                if (dso.length() > 0) {
                    RecordFormat dsoutRecFormat = getRecordFormat(dso);
                    logger.debug("dsoutRecFormat name : " + dsoutRecFormat.getName());
                    Record newRec = getRecord(dsoutRecFormat, aRequest, bI);
                    if (newRec != null) {
                        pgmCallRes.setSerializationSize(newRec.getRecordLength());
                        ArrayList<ISeriesFieldValue> values = getFieldValuesFromRecord(newRec);
                        pgmCallRes.setValues(values);
                    }

                    bI += pgmCallRes.getSerializationSize();
                }
            }
            res.add(pgmCallRes);
        }

        return res;
    }

    public ArrayList<ProgramCallRequestDTO> showISYInput(String aVal) throws Exception {
        ArrayList<ProgramCallRequestDTO> res = new ArrayList<>();
        byte[] aRequest = getDigits(aVal);
        int bI = 0;
        while (bI < aRequest.length) {
            int bMessageType = aRequest[bI];
            if (bMessageType != 1 && bMessageType != 2) {
                logger.error("Tipo messaggio non valido per showISYInput: " + bMessageType);
                break;
            }
            bI += 1;
            int bMessageID = (256 * 256 * 256 * Byte.toUnsignedInt(aRequest[bI])) + (256 * 256 * Byte.toUnsignedInt(aRequest[bI + 1])) + (256 * Byte.toUnsignedInt(aRequest[bI + 2])) + Byte.toUnsignedInt(aRequest[bI + 3]);
            bI += 4;
            int bMessageLen = (256 * 256 * 256 * Byte.toUnsignedInt(aRequest[bI])) + (256 * 256 * Byte.toUnsignedInt(aRequest[bI + 1])) + (256 * Byte.toUnsignedInt(aRequest[bI + 2])) + Byte.toUnsignedInt(aRequest[bI + 3]);
            if (bMessageLen > aRequest.length || bMessageLen < 0) {
                logger.error("Messagge Len non valida per showISYInput: " + bMessageLen);
                break;
            }
            bI += 4;
            logger.debug("====================================================================>\n");
            logger.debug("messageType : " + bMessageType);
            logger.debug("messageID   : " + bMessageID);
            logger.debug("messageLen  : " + bMessageLen);
            ProgramCallRequestDTO callreq = getCallRequest(bMessageType, aRequest, bI, bMessageLen, bMessageID);
            res.add(callreq);
            bI += bMessageLen;
        }

        return res;
    }

    // Estrae i byte dal testo del messaggio che e' nella forma "Hex string: 0x.. 0x.. "
    byte[] getDigits(String aText) {
        String constantString = "Hex string: ";
        String bText = aText.trim();
        if (bText.indexOf(constantString) > 0) {
            bText = aText.substring(aText.indexOf(constantString) + constantString.length()).trim();
            String[] digitStrings = bText.split(" ");
            byte[] digits = new byte[digitStrings.length];
            for (int i = 0; i < digitStrings.length; i++) {
                String d = digitStrings[i].replace("0x", "").trim();
                byte c=0;
                try{
                c = (byte) Integer.parseInt(d, 16);
                } catch (NumberFormatException e) {
                    logger.error("getDigits error: " + d + " in not a number");
                }
                digits[i] = c;
            }
            return digits;
        }
        return new byte[0];
    }

    CharsetDecoder getEBCDIC2ASCIIDecoder() {
        return Charset.forName("IBM01144").newDecoder();
    }

    // // Torna un RecordFormat dato il nome della struttura DS
    RecordFormat getRecordFormat(String aDsName) throws Exception {        
        FFDResponseDTO ffd = _objectUtil.getFFD("", aDsName);
        RecordFormat fmt = new RecordFormat();
        fmt.setName(aDsName);
        for (ISeriesFieldDescription fld : ffd.getFields()) {
            if (fld.getFieldType().equals("A")
                    || fld.getFieldType().equals("L")
                    || fld.getFieldType().equals("T")) {
                fmt.addFieldDescription(
                        new CharacterFieldDescription(new AS400Text(fld.getFieldLength()), fld.getFieldName()));
            } else if (fld.getFieldType().equals("S")) {
                fmt.addFieldDescription(
                        new ZonedDecimalFieldDescription(
                                new AS400ZonedDecimal(fld.getFieldLength(), fld.getFieldScale()),
                                fld.getFieldName()));
            } else if (fld.getFieldType().equals("P")) {
                fmt.addFieldDescription(
                        new PackedDecimalFieldDescription(
                                new AS400PackedDecimal(fld.getFieldLength(), fld.getFieldScale()),
                                fld.getFieldName()));
            }
        }
        return fmt;
    }

    // Dato un formato record e un array di byte, torna il record popolato
    Record getRecord(RecordFormat aRecFmt, byte[] someBytes, int aStartIndex) throws UnsupportedEncodingException {
        Record newRec = aRecFmt.getNewRecord();
        int bytePos = aStartIndex;
        for (int idx = 0; idx < aRecFmt.getNumberOfFields(); idx++) {
            FieldDescription f = aRecFmt.getFieldDescription(idx);
            AS400DataType dType = f.getDataType();
            byte[] bFieldValuesBytes = new byte[dType.getByteLength()];
            try {
                System.arraycopy(someBytes, bytePos, bFieldValuesBytes, 0, dType.getByteLength());
                if (dType instanceof AS400PackedDecimal) {
                    AS400PackedDecimal packedType = (AS400PackedDecimal) dType;
                    Number numValue = (Number) packedType.toObject(bFieldValuesBytes);
                    newRec.setField(idx, numValue);
                } else if (dType instanceof AS400ZonedDecimal) {
                    AS400ZonedDecimal zonedType = (AS400ZonedDecimal) dType;
                    Number numValue = (Number) zonedType.toObject(bFieldValuesBytes);
                    newRec.setField(idx, numValue);
                } else if (dType instanceof AS400Text) {
                    AS400Text textType = new AS400Text(dType.getByteLength());
                    String fieldValue = textType.toObject(bFieldValuesBytes).toString();
                    newRec.setField(idx, fieldValue);
                } else if (dType instanceof AS400Date) {
                    AS400Text textType = new AS400Text(dType.getByteLength());
                    String fieldValue = textType.toObject(bFieldValuesBytes).toString();
                    newRec.setField(idx, fieldValue);
                } else if (dType instanceof AS400Time) {
                    AS400Text textType = new AS400Text(dType.getByteLength());
                    String fieldValue = textType.toObject(bFieldValuesBytes).toString();
                    newRec.setField(idx, fieldValue);
                } else {
                    logger.error("Tipo non gestito: " + dType.getClass().getName());
                }
                logger.debug("Field " + idx + " name: " + f.getFieldName() + " value: " + newRec.getField(idx).toString());
            } catch (ClassCastException | ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                logger.error("Errore nella conversione del campo " + aRecFmt.getName() + "." + f.getFieldName(), ex);
            }
            bytePos += dType.getByteLength();
        }
        return newRec;
    }

    ArrayList<ISeriesFieldValue> getFieldValuesFromRecord(Record aRec) throws Exception {
        RecordFormat aRecFormat = aRec.getRecordFormat();
        ArrayList<ISeriesFieldValue> values = new ArrayList<>();        
        // Serve per prendere le descrizioni dei campi
        FFDResponseDTO ffd = _objectUtil.getFFD("", aRecFormat.getName());

        for (int idx = 0; idx < aRecFormat.getNumberOfFields(); idx++) {
            FieldDescription f = aRecFormat.getFieldDescription(idx);
            AS400DataType dType = f.getDataType();
            ISeriesFieldValue newVal = new ISeriesFieldValue();
            newVal.setName(f.getFieldName());
            newVal.setLength(dType.getByteLength());
            newVal.setScale(0);
            newVal.setDescription(_objectUtil.getFiedDescription(ffd.getLibrary(), aRecFormat.getName(), f.getFieldName()));
            if (dType instanceof AS400PackedDecimal) {
                PackedDecimalFieldDescription pdFld = (PackedDecimalFieldDescription) f;
                newVal.setScale(pdFld.getDecimalPositions());
                newVal.setType("P");
            } else if (dType instanceof AS400ZonedDecimal) {
                ZonedDecimalFieldDescription zdFld = (ZonedDecimalFieldDescription) f;
                newVal.setScale(zdFld.getDecimalPositions());
                newVal.setType("S");
            } else if (dType instanceof AS400Text) {
                newVal.setType("A");
            } else if (dType instanceof AS400Date) {
                newVal.setType("L");
            } else if (dType instanceof AS400Time) {
                newVal.setType("T");
            }
            Object fieldObj = aRec.getField(idx);
            if (fieldObj != null) {
                newVal.setValue(fieldObj.toString());
            } else {
                logger.error("Impossibile impostare il valore del campo " + f.getFieldName());
            }
            values.add(newVal);
        }
        return values;
    }

    ProgramCallRequestDTO getCallRequest(int aMessageType, byte[] aRequest, int aStartIndex, int aMessageLen, int aMessageId) throws Exception {
        CharsetDecoder ebcdic2asciiDecoder = getEBCDIC2ASCIIDecoder();
        ProgramCallRequestDTO pgmCallReq = null;
        int bI = aStartIndex;
        if (aMessageType == 1 || aMessageType == 2) {
            String pgm = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 10)).toString().trim();
            bI += 10;
            String cmd = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;
            String fio = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 1)).toString().trim();
            bI += 1;
            String dsi = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;
            String dso = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;
            String cid = ebcdic2asciiDecoder.decode(java.nio.ByteBuffer.wrap(aRequest, bI, 8)).toString().trim();
            bI += 8;
            int times = (256 * aRequest[bI]) + (aRequest[bI + 1]);
            bI += 2;
            if (aMessageType == 1) {
                logger.debug("pgm         : " + pgm);
            }
            if (aMessageType == 2) {
                logger.debug("file        : " + pgm + ". pgm:ZZDOG");
            }
            logger.debug("cmd         : " + cmd);
            logger.debug("fio         : " + fio);
            logger.debug("dsi         : " + dsi);
            logger.debug("dso         : " + dso);
            logger.debug("cid         : " + cid);
            logger.debug("times       : " + times);
            pgmCallReq = new ProgramCallRequestDTO();
            DSPOBJDRequestDTO pgmInfoReq = new DSPOBJDRequestDTO("*LIBL", pgm, "*PGM");
            DSPOBJDResponseDTO pgmInfoRes = _objectUtil.dspobjd(pgmInfoReq);

            pgmCallReq.setProgram(pgm + " [" + pgmInfoRes.getDescription() + "]");
            pgmCallReq.setCommand(cmd);
            pgmCallReq.setDsin(dsi);
            pgmCallReq.setDsout(dso);
            pgmCallReq.setFlagIO(fio);
            pgmCallReq.setTimes(Integer.toString(times));
            if (dsi.length() > 0) {
                RecordFormat dsinRecFormat = getRecordFormat(dsi);
                if (dsinRecFormat != null) {
                    logger.debug("dsinRecFormat name : " + dsinRecFormat.getName());
                    Record newRec = getRecord(dsinRecFormat, aRequest, bI);
                    if (newRec != null) {
                        ArrayList<ISeriesFieldValue> values = getFieldValuesFromRecord(newRec);                
                        pgmCallReq.setValues(values);
                    }
                    else {
                        logger.error("newRec is null for name : " + dsi);
                    }
                }
                else {
                    logger.error("dsinRecFormat is null for name : " + dsi);
                }   

            }
        }
        return pgmCallReq;
    }
}
