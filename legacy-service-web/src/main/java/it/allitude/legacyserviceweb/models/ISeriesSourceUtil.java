package it.allitude.legacyserviceweb.models;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileReader;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.MemberList;
import com.ibm.as400.access.QSYSObjectPathName;

import it.allitude.legacyserviceweb.DTOs.GetSourceListResponseItem;
import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesSourceUtil {

    private static final Logger log = LoggerFactory.getLogger(ISeriesSourceUtil.class);

    ConnectionService _connectionService;
    Random _rand;

    public ISeriesSourceUtil(ConnectionService anISeriesConnectionService) {
        this._connectionService = anISeriesConnectionService;
        _rand = new Random();
    }

    String getAliasName(String sourceMemberName) {
        int upperbound = 100;
        int int_random = _rand.nextInt(upperbound);

        String res = String.format("%s%d", sourceMemberName, int_random);
        return res;
    }

    public ArrayList<GetSourceListResponseItem> getSourceList(String library, String sourceFile,
            String sourceMemberName) throws Exception {
        AS400 as = _connectionService.getAS400Connection();
        QSYSObjectPathName path = new QSYSObjectPathName(library, sourceFile, sourceMemberName, "MBR");
        MemberList srcfile = new MemberList(as, path);
        srcfile.load();
        MemberDescription[] list = srcfile.getMemberDescriptions();
        ArrayList<GetSourceListResponseItem> res = new ArrayList<GetSourceListResponseItem>();
        for (MemberDescription memberDescription : list) {
            GetSourceListResponseItem newElement = new GetSourceListResponseItem(library, sourceFile,
                    memberDescription.getValue(MemberDescription.MEMBER_NAME).toString(),
                    memberDescription.getValue(MemberDescription.MEMBER_TEXT_DESCRIPTION).toString()
                    );
            res.add(newElement);
        }               
        return res;
    }

    public static ArrayList<String> getChunks(String s, int chunkSize) {
        ArrayList<String> chunks = new ArrayList<>();
        StringBuilder sb = new StringBuilder(s);

        while (!(sb.length() == 0)) {
            chunks.add(sb.substring(0, chunkSize));
            sb.delete(0, chunkSize);

        }
        return chunks;
    }

 

    static Hashtable<String, ArrayList<String>> g_source_cache = new Hashtable<String, ArrayList<String>> ();

    private static String getCacheKey(String library, String sourceFile, String sourceMemberName, boolean explodeCOPY)
    {        
        return String.format("%s_%s_%s_%s_%d_", library, sourceFile, sourceMemberName, explodeCOPY ? "T" : "F", LocalTime.now().getHour());
    }

    private ArrayList<String> getSourceFromCache(String library, String sourceFile, String sourceMemberName, boolean explodeCOPY) throws Exception
    {
        String key = getCacheKey(library, sourceFile, sourceMemberName, explodeCOPY);
        if (g_source_cache.containsKey(key))
            return g_source_cache.get(key);
        return null;
    }

    private void addSourceToCache(String library, String sourceFile, String sourceMemberName, ArrayList<String> source, boolean explodeCOPY)
    {
        String key = getCacheKey(library, sourceFile, sourceMemberName, explodeCOPY);
        if (g_source_cache.contains(key))
            return;
        else
            g_source_cache.put(key, source);
    }
    public ArrayList<String> getSource(String library, String sourceFile, String sourceMemberName, boolean explodeCOPY)
            throws Exception {

        ArrayList<String> lines = getSourceFromCache(library, sourceFile, sourceMemberName, explodeCOPY);
        if (lines != null)
            return lines;
        
        lines = new ArrayList<String>();
        String sql;
        Connection aConn = _connectionService.getAS400JdbcConnection();
        Statement stmt = aConn.createStatement();      

        String aliasName = getAliasName(sourceMemberName);
        sql = String.format("DROP ALIAS QTEMP.%s", aliasName);
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }

        sql = String.format("CREATE ALIAS QTEMP.%s FOR %s.%s(%s)", aliasName, library, sourceFile, sourceMemberName);
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            return null;
        }

        ResultSet rs;
        try {
            sql = String.format("SELECT  SRCSEQ, SRCDAT, SUBSTR(SRCDTA,6,99) AS CODE FROM QTEMP.%s ORDER BY SRCSEQ", aliasName);
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            return null;
        }

        if (rs == null)
            return null;
    
        while (rs.next()) {
            lines.add(rs.getString(3).substring(0, 80) + " - " + String.format("%1$6s", rs.getString(2)).replace(' ', '0'));
        }       

        sql = String.format("DROP ALIAS QTEMP.%s", aliasName);
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }

        if (!explodeCOPY) {
            addSourceToCache(library, sourceFile, sourceMemberName, lines, explodeCOPY);
            return lines;
        }

        ArrayList<String> explodedLines = new ArrayList<String>();

        for (String line : lines) {
            boolean isCommentLine = line.charAt(1) == '*';
            int indexOfCOPY = line.indexOf("/COPY");
            if (indexOfCOPY > 0 && !isCommentLine) {
                line = line.substring(indexOfCOPY + "/COPY".length() + 1);
                String[] parts = line.substring(0, 25).replace("/COPY", "").trim().split(",");
                String copyFile = parts[0].trim();
                String copyMember = parts[1].trim();
                if (copyFile.length() > 11 || copyMember.length() > 11)
                    throw new Exception(
                            String.format("Copy file [%s] or Member [%s] not Valid !!!! ", copyFile, copyMember));
                ArrayList<String> copyLines = getSource("LIBFC", copyFile, copyMember, false);

                if (copyLines == null) // non ho trovato il sorgente della /COPY
                    explodedLines.add(" " + line);
                else {
                    explodedLines.add("++++++>>>>>>> [" + copyMember + "] " + line.substring(0, 25).trim());
                    copyLines.forEach((l) -> explodedLines.add("+" + l));
                    explodedLines.add("++++++<<<<<<< [" + copyMember + "] " + line.substring(0, 25).trim());
                }
            } else {
                explodedLines.add(" " + line);
            }

        }
        return explodedLines;
    }


}
