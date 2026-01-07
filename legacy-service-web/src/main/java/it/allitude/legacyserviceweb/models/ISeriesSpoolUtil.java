package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesSpoolUtil {

    ConnectionService _connectionService;

    private static final Logger log = LoggerFactory.getLogger(ISeriesSourceUtil.class);

    @Autowired
    public ISeriesSpoolUtil(ConnectionService anISeriesConnectionService) {
        this._connectionService = anISeriesConnectionService;
    }

    public ArrayList<SpoolFileListItem> getSpoolList(String anUser) throws Exception {
        ArrayList<SpoolFileListItem> res = new ArrayList<>();
        Connection con = _connectionService.getAS400JdbcConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT "
                + " SPOOLED_FILE_NAME AS FNAME"
                + ", SPOOLED_FILE_NUMBER AS FNUMBER"
                + ", STATUS AS STATUS "
                + ", CREATION_TIMESTAMP AS CREATION_TS"
                + ", USER_DATA AS USERDATA"
                + ", SIZE AS SIZE"
                + ", TOTAL_PAGES AS PAGES"
                + ", JOB_NAME AS JNAME"
                + ", JOB_NUMBER AS JNUMBER"
                + ", JOB_USER AS JUSER"
                + ", OUTPUT_QUEUE_LIBRARY AS QLIBRARY"
                + ", OUTPUT_QUEUE AS QNAME"
                + "     FROM TABLE(QSYS2.SPOOLED_FILE_INFO(USER_NAME => '%s'))";
        sql = String.format(sql, anUser.toUpperCase());

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            SpoolFileListItem si = new SpoolFileListItem();
            si.setSpoolfileName(rs.getString("FNAME"));
            si.setSpoolNumber(rs.getInt("FNUMBER"));
            si.setStatus(rs.getString("STATUS"));
            si.setCreation_ts(rs.getTimestamp("CREATION_TS").toString());
            si.setUserData(rs.getString("USERDATA"));
            si.setSize(rs.getInt("SIZE"));
            si.setPages(rs.getInt("PAGES"));
            si.setJobName(rs.getString("JNAME"));
            si.setJobName(rs.getString("JNAME"));
            si.setJobNumber(rs.getString("JNUMBER"));
            si.setJobUser(rs.getString("JUSER"));
            si.setOutputQueueLibrary(rs.getString("QLIBRARY"));
            si.setOutputQueueName(rs.getString("QNAME"));
            res.add(si);
        }

        return res;
    }

    public ArrayList<String> getSpoolItem(String jobname, String spoolName, String spoolNumber) throws Exception {
        ArrayList<String> res = new ArrayList<String>();
        Connection con = _connectionService.getAS400JdbcConnection();
        Statement stmt = con.createStatement();
        String sql = String.format(
                " SELECT SPOOLED_DATA FROM TABLE(SYSTOOLS.SPOOLED_FILE_DATA(JOB_NAME => '%s', SPOOLED_FILE_NAME => '%s', SPOOLED_FILE_NUMBER => %s))",
                jobname /* '212439/FC0382/FCTXF' */, spoolName, spoolNumber);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String line = StringUtils.stripEnd(rs.getString("SPOOLED_DATA"), null);
            res.add(line);
        }
        return res;
    }

    public void deleteSpoolItem(String jobname, String spoolName, String spoolNumber) throws Exception {
        Connection con = _connectionService.getAS400JdbcConnection();
        java.sql.Statement stmt = con.createStatement();
        // String sql = String.format("CALL QCMDEXC('ADDLIBLE %s')", library);
        // DLTSPLF FILE(QPDZDTALOG) JOB(266398/FC0382/FCTXE) SPLNBR(1)
        String sql = String.format("CALL QCMDEXC('DLTSPLF FILE(%s) JOB(%s) SPLNBR(%s)')", spoolName, jobname,
                spoolNumber);
        stmt.executeUpdate(sql);

    }

    public void deleteAllSpools(String user) throws Exception {

        if (user != null && !user.trim().isEmpty()) {
            Connection con = _connectionService.getAS400JdbcConnection();
            java.sql.Statement stmt = con.createStatement();
            String sql = String.format("CALL QCMDEXC('DLTSPLF FILE(*SELECT) SELECT(%s) ')", user);
            stmt.executeUpdate(sql);
        }
    }
}
