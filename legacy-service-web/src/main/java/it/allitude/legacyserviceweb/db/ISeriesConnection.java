package it.allitude.legacyserviceweb.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCConnection;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;

import it.allitude.legacyserviceweb.dsdef.ZZdDAT;
import it.allitude.legacyserviceweb.dsdef.ZZdPRM;

public class ISeriesConnection {

    private static final Logger logger = LoggerFactory.getLogger(ISeriesConnection.class);

    String _iseries_user;
    String _iseries_password;
    String _iseries_session;
    String _iseries_name;

    AS400JDBCConnection _as400jdbcconnection;

    String _libUDT;
    String _libUPC;
    String _libUTM;
    String _libMultibanca;
    String _targaCassaAmbiente;

    LocalDateTime connectionTime;

    public LocalDateTime getConnectionTime() {
        return connectionTime;
    }

    public ISeriesConnection(String iseries_name, String iseries_user, String iseries_password,
            String iseries_session) {
        _iseries_name = iseries_name;
        _iseries_user = iseries_user;
        _iseries_password = iseries_password;
        _iseries_session = iseries_session;
        logger.info("Connection initialized for " + iseries_name + "_" + iseries_user + "_" + iseries_session);
    }

    public void close() {
        if (_as400jdbcconnection != null)
            try {
                _as400jdbcconnection.getSystem().disconnectAllServices();
                _as400jdbcconnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public AS400 getAS400Connection() throws Exception {
        return getConnection().getSystem();
    }

    public Connection getAS400JdbcConnection() throws Exception {
        return getConnection();
    }

    private AS400JDBCConnection getConnection() throws Exception {
        if (_as400jdbcconnection == null || _as400jdbcconnection.isClosed()) {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            String jdbcConnString = "jdbc:as400://" + _iseries_name + ";prompt=false;";
            _as400jdbcconnection = (AS400JDBCConnection) DriverManager.getConnection(jdbcConnString, _iseries_user, _iseries_password);

            // Chiamo il programma ZZCFGADL sulla connessione jdbc per aggiungere le librerie della banca al job QZDASOINIT
            java.sql.Statement stmt = _as400jdbcconnection.createStatement();
            String sql = String.format("CALL QCMDEXC('CALL PGM(ZZCFGADL) PARM(''%s'') ')", _iseries_session);
            try {
                stmt.executeUpdate(sql);
            } catch (Exception e) {
            }
            // Chiamo il programma ZZCFGADD per creare il nuovo job (QZRCSRVS) e impostarlo con le librerie e il terminale
            callZZCFGADD();

            connectionTime = LocalDateTime.now();
        }
        return _as400jdbcconnection;
    }

    private void callZZCFGADD() throws Exception {
        boolean isSuccess;
        CommandCall ccNewCmd;
        ccNewCmd = new CommandCall(_as400jdbcconnection.getSystem());

        String strCmd = String.format("CALL PGM(ZZCFGADL) PARM('%-10s')", _iseries_session);
        try {
            Job cmdJob = ccNewCmd.getServerJob();
            isSuccess = ccNewCmd.run(strCmd);
            if (!isSuccess) {
                logger.error("Errore in fase di chiamata al programma che imposta le librerie");
                throw new Exception("Error calling ZZCFGADD...[2]");
            }
            String[] libl = cmdJob.getUserLibraryList();
            for (String lib : libl) {
                if (lib.trim().endsWith("UDT")) {
                    _libUDT = lib.trim();
                    _targaCassaAmbiente = lib.trim().substring(2, 5);
                } else if (lib.trim().endsWith("UPC"))
                    _libUPC = lib.trim();
                else if (lib.trim().endsWith("UTM"))
                    _libUTM = lib.trim();
                else if (lib.trim().endsWith("UMB"))
                    _libMultibanca = lib.trim();
            }
        } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException
                | PropertyVetoException | ObjectDoesNotExistException e) {
            e.printStackTrace();
            throw (new Exception(e));
        }

        // Chiamo il programma zzcfgrtv con il comando SET per impostare il terminale
        ZZdPRM prmRecFmt = new ZZdPRM();
        Record prmRec = prmRecFmt.getNewRecord();
        prmRec.setField(ZZdPRM.ZZPCMD, "SET");
        prmRec.setField(ZZdPRM.ZZPDSI, "ZZCFG$DS");

        ZZdDAT datRecFmt = new ZZdDAT();
        Record prmDat = datRecFmt.getNewRecord();
        String input = String.format("%-10s%-8s", _iseries_session.toUpperCase(), _iseries_user.toUpperCase());
        prmDat.setField(ZZdDAT.ZZDO01, input);

        ProgramParameter[] parmList = new ProgramParameter[2];
        parmList[0] = new ProgramParameter(prmRec.getContents(), 36);
        parmList[1] = new ProgramParameter(prmDat.getContents(), 3840);

        ProgramCall pgm = new ProgramCall(_as400jdbcconnection.getSystem(),
                QSYSObjectPathName.toPath("*LIBL", "ZZCFGRTV", "PGM"),
                parmList);
        if (pgm.run() != true) {
            AS400Message[] messageList = pgm.getMessageList(); // Errore !!
            logger.info("messageList.length is " + messageList.length);
            throw (new Exception("Error calling ZZCFGRTV"));
        }
    }
}
