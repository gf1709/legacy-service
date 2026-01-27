package it.allitude.legacyserviceweb.models;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileFilter;
import com.ibm.as400.access.IFSFileReader;
import com.ibm.as400.access.IFSTextFileOutputStream;

import it.allitude.legacyserviceweb.DTOs.IFSListFileResponseDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesIFSUtil {

    private final AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ISeriesIFSUtil.class);
    private final ConnectionService _connectionService;

    public ISeriesIFSUtil(ConnectionService anISeriesConnectionService, AppConfig appConfig, AmbientiBanca ambientiBanca, ISeriesObjectUtil objectUtil) {
        this._connectionService = anISeriesConnectionService;
        this.appConfig = appConfig;
    }

    public IFSListFileResponseDTO listFiles(String aDirectory, String aPattern) throws Exception {
        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile directory = new IFSFile(as400, aDirectory);
        if (!directory.exists()) {
            throw new FileNotFoundException("The directoy " + directory + " does not exist");
        }
        String absDirPath = org.springframework.util.StringUtils.cleanPath(aDirectory);
        IFSListFileResponseDTO res = new IFSListFileResponseDTO(absDirPath);
        IFSFile[] directoryFiles = directory.listFiles(new MyDirectoryFilter(), aPattern);
        if (directoryFiles == null) {
            log.error("The directory does not exist");
            return res;
        } else if (directoryFiles.length == 0) {
            log.info("The directory is empty");
            return res;
        }
        for (IFSFile f : directoryFiles) {
            String fname = f.getName();
            long changeDate = f.lastModified();
            java.util.Date cdate = new java.util.Date(changeDate);

            String type = "f";
            if (f.isDirectory()) {
                type = "d";
            }
            long size = f.length();
            res.addItem(fname, type, size, simpleDateFormat.format(cdate));
        }
        return res;
    }

    public ArrayList<String> getIFSFileContent(String aFileName) throws Exception {
        String line;
        ArrayList<String> res = new ArrayList<>();
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile file = new IFSFile(as400, aFileName);
        try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
            while ((line = reader.readLine()) != null) {
                res.add(line);
            }
        }
        return res;
    }

    // Comprimo LINESxCHUNCK righe per volta
    public ArrayList<String> getIFSFileContentZipped(String aFileName) throws Exception {
        final String SEPARATOR = "\r\r\t\n\nGREG\r\r\t\n\n";
        final int LINESxCHUNCK = 200;
        ArrayList<String> result = new ArrayList<>();
        String line, linesToCompress = "";
        int lineNr = 0;
        byte[] buffer = new byte[262144];
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile file = new IFSFile(as400, aFileName);
        try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
            while ((line = reader.readLine()) != null) {
                {
                    if (lineNr % LINESxCHUNCK == 0) {
                        Deflater deflater = new Deflater();
                        deflater.setInput(linesToCompress.getBytes());
                        deflater.finish();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        while (!deflater.finished()) {
                            int compressedSize = deflater.deflate(buffer);
                            outputStream.write(buffer, 0, compressedSize);
                        }
                        result.add(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
                        linesToCompress = "";
                    }
                    linesToCompress += line + SEPARATOR;
                    lineNr += 1;
                }
            }
            // comprimo le rimanenti righe            
            if (linesToCompress.length() > 0) {
                Deflater deflater = new Deflater();
                deflater.setInput(linesToCompress.getBytes());
                deflater.finish();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while (!deflater.finished()) {
                    int compressedSize = deflater.deflate(buffer);
                    outputStream.write(buffer, 0, compressedSize);
                }
                result.add(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            }
        }
        return result;
    }

    public ArrayList<String> findSibankCall(ArrayList<String> aFileNames) throws Exception {
        ArrayList<String> res = new ArrayList<>();
        if (aFileNames.size() < 1) {
            return res;
        }

        AS400 as400 = _connectionService.getAS400Connection();
        String pattern = "yyyy_MM_dd_HH_mm_ss_SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        java.util.Date now = new java.util.Date();
        String nowString = simpleDateFormat.format(now);
        IFSFile f = new IFSFile(as400, aFileNames.get(0));
        String outFile = f.getParent() + "/sibank_call_list_" + nowString + ".log";
        new Thread(new Runnable() {
            public void run() {
                try {
                    findSibankCallAsyncConcise(as400, aFileNames, outFile);
                } catch (Exception e) {
                    log.error("Error executing findSibankCallAsync. " + e.getMessage());
                }
            }
        }).start();
        res.add("Source scan is executing. Result file is " + outFile);
        return res;
    }

    private void findSibankCallAsyncConcise(AS400 as400, ArrayList<String> aFileNames, String outFileName) throws Exception {
        IFSFile f = new IFSFile(as400, aFileNames.get(0));
        IFSTextFileOutputStream outFile = new IFSTextFileOutputStream(as400, outFileName);
        outFile.write("========================================================================================================" + "\n");
        outFile.write("Scan for SIbank call. File scanned: " + "\n");
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            outFile.write(file.getAbsolutePath() + "\n");
        }
        outFile.write("========================================================================================================" + "\n");
        String line, msg = "";

        for (String fName : aFileNames) {
            int lineNr = 0;
            IFSFile file = new IFSFile(as400, fName);
            try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
                while ((line = reader.readLine()) != null) {
                    {
                        if (line.contains("[5c] - - XAM Call - Service program chiamato :") || line.contains("[5c] - Isy Call - Programmi chiamati :")) {
                            msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                            outFile.write(msg);
                        }
                        lineNr += 1;
                    }
                }
            }
        }
        outFile.close();
    }

    private void findSibankCallAsyncVerbose(AS400 as400, ArrayList<String> aFileNames, String outFileName) throws Exception {
        IFSFile f = new IFSFile(as400, aFileNames.get(0));

        IFSTextFileOutputStream outFile = new IFSTextFileOutputStream(as400, outFileName);
        outFile.write("========================================================================================================" + "\n");
        outFile.write("Scan for SIbank call. File scanned: " + "\n");
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            outFile.write(file.getAbsolutePath() + "\n");
        }
        outFile.write("========================================================================================================" + "\n");
        String line, msg = "";

        for (String fName : aFileNames) {
            int lineNr = 0;
            IFSFile file = new IFSFile(as400, fName);
            try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
                while ((line = reader.readLine()) != null) {
                    {
                        if (line.contains("XAM Call") || line.contains("Protocollo - Messaggio XAM")) {
                            msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                            outFile.write(msg);
                            lineNr += 1;
                        } else if (line.contains("[5b] - Isy Call - Messaggio in ingresso")) {
                            msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                            outFile.write(msg);
                            lineNr += 1;
                            line = reader.readLine();
                            if (line != null) {
                                msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                                outFile.write(msg);
                                lineNr += 1;
                                line = reader.readLine();
                                if (line != null) {
                                    msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                                    outFile.write(msg);
                                    lineNr += 1;
                                }
                            }
                        } else if (line.contains("[5c] - Isy Call - Programmi chiamati")) {
                            msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                            outFile.write(msg);
                            lineNr += 1;
                            line = reader.readLine();
                            if (line != null) {
                                msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                                outFile.write(msg);
                                lineNr += 1;
                            }
                        } else if (line.contains("[5d] - Isy Call - Messaggio in uscita")) {
                            msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                            outFile.write(msg);
                            lineNr += 1;
                            line = reader.readLine();
                            if (line != null) {
                                msg = "[" + file.getName() + "-Line nr: " + lineNr + "]" + line + "\n";
                                outFile.write(msg);
                                lineNr += 1;
                            }
                        } else {
                            lineNr += 1;
                        }
                    }
                }
            }
        }
        outFile.close();
    }

    public ArrayList<String> getIFSFilesContent(ArrayList<String> aFileNames) throws Exception {
        String line;
        ArrayList<String> res = new ArrayList<>();
        AS400 as400 = _connectionService.getAS400Connection();
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
                while ((line = reader.readLine()) != null) {
                    {
                        res.add("[" + fName + "] - " + line);
                    }
                }
            }
        }
        return res;
    }

    public boolean deleteIFSFile(String aFileName) throws Exception {
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile file = new IFSFile(as400, aFileName);
        return file.delete();
    }

    public boolean deleteIFSFiles(ArrayList<String> aFileNames) throws Exception {
        boolean allDeleted = true;
        AS400 as400 = _connectionService.getAS400Connection();
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            try {
                file.delete();
            } catch (Exception e) {
                log.error("Error deleting file: " + fName, e);
                allDeleted = false;
            }
        }
        return allDeleted;
    }
}

class MyDirectoryFilter implements IFSFileFilter {

    @Override
    public boolean accept(IFSFile file) {
        try {
            if ((file.getName().contains(".sock")) && (!JSession.getCurrentSession().isAdminUser())) {
                return false;
            }
            // Keep this entry.  Returning true tells the IFSList object  to return this file in the list of entries returned to the  .list() method.
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
