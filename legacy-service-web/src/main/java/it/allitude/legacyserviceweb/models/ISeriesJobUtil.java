package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.JobLog;
import com.ibm.as400.access.QueuedMessage;

import it.allitude.legacyserviceweb.DTOs.DSPOBJDRequestDTO;
import it.allitude.legacyserviceweb.DTOs.DSPOBJDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.JobDetailInfoRequestDTO;
import it.allitude.legacyserviceweb.DTOs.JobDetailInfoResponseDTO;
import it.allitude.legacyserviceweb.DTOs.JobDetailInfoResponseDTOCallStackItem;
import it.allitude.legacyserviceweb.DTOs.JobDetailInfoResponseDTOOpenFileItem;
import it.allitude.legacyserviceweb.DTOs.JobListResponseDTO;
import it.allitude.legacyserviceweb.DTOs.ServiceInfoResponseDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesJobUtil {

    private final AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ISeriesJobUtil.class);
    private final ConnectionService _connectionService;
    private final AmbientiBanca _ambientiBanca;
    private final ISeriesObjectUtil _iseriesObjectUtil;
    Random _rand;
    private final ISeriesUsers _users;

    public ISeriesJobUtil(ConnectionService anISeriesConnectionService, AppConfig appConfig, AmbientiBanca ambientiBanca, ISeriesObjectUtil objectUtil, ISeriesUsers users) {
        this._connectionService = anISeriesConnectionService;
        _rand = new Random();
        this.appConfig = appConfig;
        this._ambientiBanca = ambientiBanca;
        this._iseriesObjectUtil = objectUtil;
        this._users = users;
    }

    public ArrayList<ServiceInfoResponseDTO> getSocketServiceInfo() throws Exception {
        ArrayList<AmbienteBanca> ambienti = Collections.list(_ambientiBanca.getAmbienti());

        String sql = "";
        for (AmbienteBanca curAmb : ambienti) {
            if (sql.trim().length() > 10) {
                sql += " UNION ALL ";
            }
            sql += String.format("select '%s' as lib, z11ele as service_name, substr(z11fld, 104, 6) as port , substr(z11fld, 1, 1) as enabled  , substr(z11fld, 26, 10) as PGM from %s.z11 where z11ser = 'Z01' and z11tab = 'ZZSRV' and z11ele in ('HHJS1','HHJS2','HHJS3','HHJS4','HHJS5','HHJS6','HHTC1','HHTC5','HHTC6','HHTC7','HHTC8','JOMSG') ",
                    curAmb.getLibreriaDati(), curAmb.getLibreriaDati());
        }
        ArrayList<ServiceInfoResponseDTO> res = new ArrayList<>();
        Connection con = _connectionService.getAS400JdbcConnection();
        if (true) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String targa = rs.getString("LIB").trim().substring(2, 4);
                String ambiente = rs.getString("LIB").trim().substring(4, 5);
                String port = rs.getString("PORT").trim();
                String name = rs.getString("SERVICE_NAME").trim();
                if (!StringUtils.isAllBlank(port)) {
                    ServiceInfoResponseDTO si = new ServiceInfoResponseDTO();
                    si.setTarga(targa);
                    si.setAmbiente(ambiente);
                    si.setName(name);
                    si.setPort(port);
                    si.setEnabled(rs.getString("ENABLED").trim());
                    si.setProgram(rs.getString("PGM").trim());
                    AmbienteBanca tmp = _ambientiBanca.getAmbiente(rs.getString("LIB").trim());
                    if (tmp != null && !StringUtils.isAllBlank(tmp.getAbi())) {
                        si.setAbi(tmp.getAbi());
                        res.add(si);
                    }
                }
            }
        }

        // leggo i servizi hhtc* dal file ze5
        if (true) {
            sql = "SELECT * FROM LIBFCCFG.ZE5 WHERE ZE5ASR LIKE 'HHT%'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String targa = rs.getString("ZE5TAC").toUpperCase().trim();
                String ambiente = rs.getString("ZE5FPT").toUpperCase();
                String port = rs.getString("ZE5FL1").trim();
                String name = rs.getString("ZE5ASR").trim();
                String libUDT = "LB" + targa + ambiente + "UDT";
                if (!StringUtils.isAllBlank(port)) {
                    ServiceInfoResponseDTO si = new ServiceInfoResponseDTO();
                    si.setTarga(targa);
                    si.setAmbiente(ambiente);
                    si.setName(name);
                    si.setPort(port);
                    si.setEnabled(rs.getString("ZE5UTI").trim());
                    si.setProgram("[--ZE5--]");
                    AmbienteBanca tmp = _ambientiBanca.getAmbiente(libUDT);
                    if (tmp != null && !StringUtils.isAllBlank(tmp.getAbi())) {
                        si.setAbi(tmp.getAbi());
                        res.add(si);
                    }
                }
            }
        }
        return res;
    }

    public ArrayList<JobListResponseDTO> netstat_job_info(int aPort, String aJobUser, String aJobName) throws Exception {
        HashMap<String, JobListResponseDTO> res = new HashMap<String, JobListResponseDTO>();

        Connection con = _connectionService.getAS400JdbcConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT REMOTE_ADDRESS, REMOTE_PORT, LOCAL_ADDRESS, LOCAL_PORT, JOB_NAME_SHORT, JOB_USER, AUTHORIZATION_NAME, JOB_NUMBER FROM QSYS2.NETSTAT_JOB_INFO ";
        sql += " WHERE LOCAL_PORT = " + Integer.toString(aPort);
        String parm = aJobName.replace('*', '%').toUpperCase();
        if (parm.length() > 0) {
            if (parm.contains("%")) {
                sql += " AND JOB_NAME_SHORT LIKE '" + parm + "'";
            } else {
                sql += " AND JOB_NAME_SHORT LIKE '%" + parm + "%'";
            }
        }
        parm = aJobUser.replace('*', '%').toUpperCase();
        if (parm.length() > 0) {
            if (parm.contains("%")) {
                sql += " AND JOB_USER LIKE '" + parm + "'";
            } else {
                sql += " AND JOB_USER LIKE '%" + parm + "%'";
            }
        }
        sql += " ORDER BY JOB_NAME_SHORT";

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            // String key=rs.getString("REMOTE_ADDRESS").trim() + ":" + rs.getString("REMOTE_PORT").trim();
            String key = rs.getString("AUTHORIZATION_NAME").trim() + "_" + rs.getString("JOB_NAME_SHORT").trim() + "_" + rs.getString("JOB_NUMBER").trim();

            JobListResponseDTO resEle = null;
            if (res.containsKey(key))
                resEle = res.get(key);
            else
                resEle = new JobListResponseDTO();
            String usr = rs.getString("JOB_USER").trim();
            String usrDes = _users.getUserDescription(usr);
            String currUsr = rs.getString("AUTHORIZATION_NAME").trim();
            String currUsrDes = _users.getUserDescription(currUsr);
            resEle.setUser(usr);
            resEle.setUserDescription(usrDes);
            resEle.setCurrentUser(currUsr);
            resEle.setCurrentUserDescription(currUsrDes);

            resEle.setName(rs.getString("JOB_NAME_SHORT").trim());
            resEle.setNumber(rs.getString("JOB_NUMBER"));
            resEle.setFunction("-");
            resEle.setStatus("-");
            resEle.addRemoteAddress(rs.getString("REMOTE_ADDRESS").trim() + ":" + rs.getString("REMOTE_PORT").trim());
            res.put(key, resEle);
        }
        con.close();        
        return new ArrayList<>(res.values());
    }

    public ArrayList<JobListResponseDTO> WRKACTJOB(WRKACTJOB_Filter filter) throws Exception {
        ArrayList<JobListResponseDTO> res = new ArrayList<>();

        Connection con = _connectionService.getAS400JdbcConnection();
        String sql = "SELECT * FROM TABLE(QSYS2.ACTIVE_JOB_INFO(";

        String flt = filter.getJobName();
        if (flt != null && flt.length() > 0) {
            if (flt.endsWith("?")) {
                flt = flt.replace('?', '*');
            }
            sql += String.format(" JOB_NAME_FILTER => '%s'", flt.toUpperCase());
        } else {
            sql += " JOB_NAME_FILTER => '*ALL'";
        }

        flt = filter.getUserName();
        if (flt != null && flt.length() > 0) {
            sql += String.format(", CURRENT_USER_LIST_FILTER => '%s')) ", flt.toUpperCase());
        } else {
            sql += ")) ";
        }

        if (filter.sortByJobName && filter.sortByJobStatus) {
            sql += " ORDER BY JOB_NAME_SHORT,JOB_STATUS";
        } else if (filter.sortByJobName) {
            sql += " ORDER BY JOB_NAME_SHORT";
        } else if (filter.sortByJobStatus) {
            sql += " ORDER BY JOB_STATUS";
        }

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            JobListResponseDTO resEle = new JobListResponseDTO();

            String usr = rs.getString("JOB_USER").trim();
            String usrDes = _users.getUserDescription(usr);
            String currUsr = rs.getString("AUTHORIZATION_NAME").trim();
            String currUsrDes = _users.getUserDescription(currUsr);

            resEle.setUser(usr);
            resEle.setCurrentUser(currUsr);
            resEle.setUserDescription(usrDes);
            resEle.setCurrentUserDescription(currUsrDes);

            resEle.setName(rs.getString("JOB_NAME_SHORT").trim());
            resEle.setNumber(rs.getString("JOB_NUMBER").trim());
            resEle.setFunction(rs.getString("FUNCTION"));
            resEle.setStatus(rs.getString("JOB_STATUS").trim());
            res.add(resEle);
        }

        return res;
    }

    public void endJob(String jobName, String userName, String jobNumber) throws Exception {
        AS400 as = _connectionService.getAS400Connection();
        Job job = new Job(as, jobName, userName, jobNumber);
        job.end(0);
    }

    public ArrayList<String> getJobLog(String jobName, String userName, String jobNumber) throws Exception {
        DateFormat timeDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        ArrayList<String> res = new ArrayList<>();
        AS400 as = _connectionService.getAS400Connection();
        Job job = new Job(as, jobName, userName, jobNumber);
        JobLog joblog = job.getJobLog();
        joblog.load();
        Enumeration logs = joblog.getMessages();

        while (logs.hasMoreElements()) {
            QueuedMessage item = (QueuedMessage) logs.nextElement();
            res.add(timeDateFormat.format(item.getDate().getTime()) + " " + item.toString());
        }
        joblog.close();
        return res;
    }

    public void setJobLogVerbose(String jobName, String userName, String jobNumber) throws Exception {
        AS400 as = _connectionService.getAS400Connection();
        Job job = new Job(as, jobName, userName, jobNumber);
        job.setLoggingText(Job.LOGGING_TEXT_SECLVL);
        job.setLoggingLevel(4);
        job.setLoggingCLPrograms(Job.LOG_CL_PROGRAMS_YES);
        job.setJobSwitches("11100000");
        job.commitChanges();
    }

    String DecodeFuncionType(String fun) {
        String f = fun.trim();
        if (f.equals("C")) {
            return "CMD";
        } else if (f.equals("P")) {
            return "PGM";
        } else if (f.equals("D")) {
            return "DLY";
        } else if (f.equals("G")) {
            return "GRP";
        } else if (f.equals("I")) {
            return "IDX";
        } else if (f.equals("J")) {
            return "JVA";
        } else if (f.equals("L")) {
            return "LOG";
        } else if (f.equals("O")) {
            return "IO";
        } else if (f.equals("N")) {
            return "MNU";
        }
        return f;
    }

    public ResponseEntity<JobDetailInfoResponseDTO> getJobDetail(JobDetailInfoRequestDTO aJobDetailInfoRequestDTO) {

        String pattern = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        JobDetailInfoResponseDTO res = new JobDetailInfoResponseDTO(aJobDetailInfoRequestDTO);

        try {

            AS400 as = _connectionService.getAS400Connection();
            Job job = new Job(as, aJobDetailInfoRequestDTO.getJobName(), aJobDetailInfoRequestDTO.getUserName(),
                    aJobDetailInfoRequestDTO.getJobNumber());
            res.setJobDate(simpleDateFormat.format(job.getJobActiveDate()));
            res.setSubSystem(job.getSubsystem());
            res.setFunction(DecodeFuncionType(job.getFunctionType()) + "-" + job.getFunctionName());
            res.setStatusExtended("*" + job.getStringValue(Job.ACTIVE_JOB_STATUS));

            res.setCpuUsed(Integer.toString(job.getCPUUsed()) + " mmss");
            res.setTempStorageUsed(Integer.toString((Integer) job.getValue(Job.TEMP_STORAGE_USED) / 1000) + " MB");

            res.setLoggingText(job.getLoggingText());
            res.setLoggingLevel(job.getLoggingLevel());
            res.setLoggingCLPrograms(job.getLoggingCLPrograms());
            res.setJobSwitches(job.getJobSwitches());

            ArrayList<String> libl = new ArrayList<>();
            for (String ele : job.getSystemLibraryList()) {
                if (ele != null && ele.trim().length() > 0) {
                    libl.add("[SYS] " + ele);
                }
            }
            if (job.getCurrentLibrary() != null && job.getCurrentLibrary().trim().length() > 0) {
                libl.add("[CUR] " + job.getCurrentLibrary());
            }
            for (String ele : job.getUserLibraryList()) {
                if (ele != null && ele.trim().length() > 0) {
                    libl.add("[USR] " + ele);
                }
            }
            res.setLibraryList(libl);
            ArrayList<JobDetailInfoResponseDTOOpenFileItem> openFiles = new ArrayList<>();
            Connection con = _connectionService.getAS400JdbcConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT LIBRARY_NAME AS LIB,  FILE_NAME AS FIL,  FILE_TYPE AS TYP , IFNULL(MEMBER_NAME,' ') AS MBR";
            sql += " , ACTIVATION_GROUP_NAME AS ACTGRP, WRITE_COUNT AS WRITECOUNT, READ_COUNT AS READCOUNT";
            sql += " , RELATIVE_RECORD_NUMBER AS RRN";
            sql += " FROM TABLE(QSYS2.OPEN_FILES('%s/%s/%s'))";
            sql = String.format(sql, aJobDetailInfoRequestDTO.getJobNumber(), aJobDetailInfoRequestDTO.getUserName(),
                    aJobDetailInfoRequestDTO.getJobName());

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                JobDetailInfoResponseDTOOpenFileItem newFile = new JobDetailInfoResponseDTOOpenFileItem();
                newFile.setLibrary(rs.getString("LIB"));
                newFile.setFile(rs.getString("FIL"));
                newFile.setType(rs.getString("TYP"));
                newFile.setMember(rs.getString("MBR"));
                newFile.setActgrp(rs.getString("ACTGRP"));

                if (rs.getObject("WRITECOUNT") == null) {
                    newFile.setWriteCount("-");
                } else {
                    newFile.setWriteCount(Integer.toString(rs.getInt("WRITECOUNT")));
                }

                if (rs.getObject("READCOUNT") == null) {
                    newFile.setReadCount("-");
                } else {
                    newFile.setReadCount(Integer.toString(rs.getInt("READCOUNT")));
                }

                if (rs.getObject("RRN") == null) {
                    newFile.setRrn("-");
                } else {
                    newFile.setRrn(Integer.toString(rs.getInt("RRN")));
                }

                openFiles.add(newFile);
            }
            res.setOpenFiles(openFiles);

            // call stack
            ArrayList<JobDetailInfoResponseDTOCallStackItem> callStack = new ArrayList<JobDetailInfoResponseDTOCallStackItem>();
            sql = "SELECT REQUEST_LEVEL AS LEVEL, ";
            sql += " PROGRAM_NAME AS PROGRAM, PROGRAM_LIBRARY_NAME AS PROGRAM_LIBRARY, ";
            sql += " STATEMENT_IDENTIFIERS AS STATEMENT, MI_INSTRUCTION_NUMBER AS INSTRUCTION_NUMBER,";
            sql += " PROCEDURE_NAME AS PROCEDURE, MODULE_NAME AS MODULE, MODULE_LIBRARY_NAME AS MODULE_LIBRARY";
            sql += " FROM TABLE(QSYS2.STACK_INFO('%s/%s/%s','ALL')) ";
            sql += " WHERE PROGRAM_NAME IS NOT NULL";
            sql += " ORDER BY ORDINAL_POSITION";
            sql = String.format(sql, aJobDetailInfoRequestDTO.getJobNumber(), aJobDetailInfoRequestDTO.getUserName(),
                    aJobDetailInfoRequestDTO.getJobName());

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                JobDetailInfoResponseDTOCallStackItem callStackItem = new JobDetailInfoResponseDTOCallStackItem();

                String val = "";
                val = rs.getObject("LEVEL") == null ? "-" : rs.getString("LEVEL");
                callStackItem.setLevel(val);
                val = rs.getObject("PROGRAM") == null ? "-" : rs.getString("PROGRAM");
                callStackItem.setProgram(val);
                val = rs.getObject("PROGRAM_LIBRARY") == null ? "-" : rs.getString("PROGRAM_LIBRARY");
                callStackItem.setProgramLib(val);
                val = rs.getObject("STATEMENT") == null ? "-" : rs.getString("STATEMENT");
                callStackItem.setStatement(val);
                val = rs.getObject("INSTRUCTION_NUMBER") == null ? "-" : rs.getString("INSTRUCTION_NUMBER");
                callStackItem.setInstructionNumber(val);
                val = rs.getObject("PROCEDURE") == null ? "-" : rs.getString("PROCEDURE");
                callStackItem.setProcedure(val);
                val = rs.getObject("MODULE") == null ? "-" : rs.getString("MODULE");
                callStackItem.setModule(val);
                val = rs.getObject("MODULE_LIBRARY") == null ? "-" : rs.getString("MODULE_LIBRARY");
                callStackItem.setModuleLib(val);
                callStack.add(callStackItem);
            }
            res.setCallStack(callStack);

            // Recupero la descrizione dell'utente
            DSPOBJDRequestDTO objReq = new DSPOBJDRequestDTO("*LIBL", aJobDetailInfoRequestDTO.getUserName().toUpperCase(), "USRPRF");
            DSPOBJDResponseDTO userDesc = _iseriesObjectUtil.dspobjd(objReq);
            if (userDesc != null) {
                res.setUserDescription(aJobDetailInfoRequestDTO.getUserName().toUpperCase() + " [" + userDesc.getDescription() + "]");
            }

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }
}
