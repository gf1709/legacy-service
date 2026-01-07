package it.allitude.legacyserviceweb.controllers;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;

import it.allitude.legacyserviceweb.authentication.JwtTokenUtil;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.dsdef.A02;
import it.allitude.legacyserviceweb.models.JSession;
import it.allitude.legacyserviceweb.unit_tests.A02Call;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class EchoController {

    @Autowired
    private JwtTokenUtil _jwtTokenUtil;

    @Autowired
    private ConnectionService _connectionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/echo")
    public String echo() {
        String msg = "Echo controll response: current timestamp is " + (new Date()).toString();
        logger.info(msg);
        return msg;
    }

    @GetMapping("/echo-auth")
    public String echoauth(@RequestHeader("Authorization") String token) throws Exception {

        String user = _jwtTokenUtil.getUsernameFromToken(token);
        String session = _jwtTokenUtil.getSessionFromToken(token);
        String pwd = _jwtTokenUtil.getPasswordFromToken(token);

        String msg = String.format("Authorized echo controll response: current timestamp is %s. User=%s, Session=%s, Pwd=%s", (new Date()).toString(), user, session, pwd);

        java.sql.Connection con = _connectionService.getAS400JdbcConnection();
        String sql = "SELECT Z80SER,Z80RAP, Z80INC FROM LBFCBUDT.Z80 LIMIT 10";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            msg += "\n";
            msg += rs.getString(1) + " - " + rs.getString(2) + " - " + rs.getString(3);
        }

        // con.close();
        logger.info(msg);
        return msg;
    }

    @GetMapping("/test-A02-call")
    public String test_A02_call(@RequestHeader("Authorization") String token) throws Exception {

        A02Call call = new A02Call(_connectionService);
        String msg = call.call(token);
        return msg;
    }

    @GetMapping("/test-oracle-connection")
    public String test_oracle_connection() throws Exception {
        String res = "hello";
        for (int i = 0; i < 5; i++) {
            Connection c = _connectionService.getOracleS2AConnection();
            try (ResultSet rs = c.createStatement().executeQuery("SELECT user  VAL from dual")) {
                while (rs.next()) {
                    res = " Oracle connected user is " + rs.getString("VAL");
                }
            }
            c.close();
        }

        return res;
    }

    @GetMapping("/test-connection")
    public String test_connection() throws Exception {

        java.sql.Connection con = _connectionService.getAS400JdbcConnection();
        AS400 as400 = _connectionService.getAS400Connection();
        String token = JSession.getCurrentSession().getJwt();
        String user = _jwtTokenUtil.getUsernameFromToken(token);
        String session = _jwtTokenUtil.getSessionFromToken(token);
        String pwd = _jwtTokenUtil.getPasswordFromToken(token);

        if (as400 == null || !as400.isConnected())
            as400 = new AS400("S44B3824.fc.crtnet", user, pwd);

        String strCmd = String.format("CALL PGM(ZZCFGADL) PARM('%-10s')", session);
        try {
            CommandCall ccNewCmd;
            ccNewCmd = new CommandCall(as400);
            boolean isSuccess = ccNewCmd.run(strCmd);
            if (!isSuccess) {
                logger.error("Errore in fase di chiamata al programma che imposta le librerie");
                throw new Exception("Error calling ZZCFGADD...[2]");
            }
        } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException | PropertyVetoException | ObjectDoesNotExistException
                | ConnectionPoolException e) {
            e.printStackTrace();
        }

        String result = "";
        ProgramParameter[] parmList = new ProgramParameter[2];
        AS400Text prmtext = new AS400Text(36);
        parmList[0] = new ProgramParameter(prmtext.toBytes(" READ"), 36);
        AS400Text dattext = new AS400Text(3840);
        parmList[1] = new ProgramParameter(dattext.toBytes(" "), 3840);
        ProgramCall pgm = new ProgramCall(as400, QSYSObjectPathName.toPath("LIBFC", "A02A", "PGM"), parmList);
        try {
            if (pgm.run() != true) {
                AS400Message[] messageList = pgm.getMessageList(); // Errore !!
                logger.info("messageList.length is " + messageList.length);
            } else {
                Record a02rec = A02.from(parmList[1].getOutputData()); // OK
                String name = ((String) a02rec.getField(A02.A02COG)).trim();
                name += ", " + ((String) a02rec.getField(A02.A02NOM)).trim();
                logger.info("name.............: " + name);
                result += name + " | ";
            }
        } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException | ObjectDoesNotExistException | PropertyVetoException e) {
            e.printStackTrace();
        }

        if (con == null || con.isClosed()) {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            con = DriverManager.getConnection("jdbc:as400://S44B3824.fc.crtnet", user, pwd);
        }
        String sql = "SELECT Z80SER,Z80RAP, Z80INC FROM LBFCBUDT.Z80 LIMIT 10";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            result += "\n";
            result += rs.getString(1) + " - " + rs.getString(2) + " - " + rs.getString(3);
            logger.info(result);
        }

        return result;
    }

}
