package it.allitude.legacyserviceweb.unit_tests;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;

import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.dsdef.A02;

public class A02Call {
    
    private static final Logger logger = LoggerFactory.getLogger(A02Call.class);
   
    private ConnectionService _connectionService;
    
    public A02Call(ConnectionService anISeriesConnectionService) {
        _connectionService = anISeriesConnectionService;
    }

    public String call(String authToken)  throws Exception {
        String result="";
        ProgramParameter[] parmList = new ProgramParameter[2];
        AS400Text prmtext = new AS400Text(36);
        parmList[0] = new ProgramParameter(prmtext.toBytes(" READ"), 36);
        AS400Text dattext = new AS400Text(3840);
        parmList[1] = new ProgramParameter(dattext.toBytes(" "), 3840);
        for (int x = 0; x < 10; x++) {
            ProgramCall pgm = new ProgramCall(_connectionService.getAS400Connection(), QSYSObjectPathName.toPath("LIBFC", "A02A", "PGM"), parmList);

            try {
                if (pgm.run() != true) {                    
                    AS400Message[] messageList = pgm.getMessageList();  // Errore !!                    
                    logger.info("messageList.length is " + messageList.length);
                } else {
                    Record a02rec = A02.from(parmList[1].getOutputData()); // OK !!
                    String name = ((String) a02rec.getField(A02.A02COG)).trim();
                    name += ", " + ((String) a02rec.getField(A02.A02NOM)).trim();
                    logger.info("name.............: " + name);
                    result+= name + " | ";
                }
            } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException
                    | ObjectDoesNotExistException | PropertyVetoException e) {
                e.printStackTrace();
            }                        
        }
        return result;
    }
}
