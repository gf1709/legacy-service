package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class AmbientiBanca {

    private final AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ISeriesJobUtil.class);
    private final ConnectionService _connectionService;
    private final Dictionary<String, AmbienteBanca> g_ambienti = new Hashtable<>();
    private final Dictionary<String, String> g_abi = new Hashtable<>();

    public AmbientiBanca(ConnectionService anISeriesConnectionService, AppConfig appConfig) {
        this._connectionService = anISeriesConnectionService;
        this.appConfig = appConfig;
    }

    // boolean isValidUDTLibrary(String lib) {
    //     boolean res = false;
    //     try {
    //         Connection con = _connectionService.getAS400JdbcConnection();
    //         Statement stmt;
    //         stmt = con.createStatement();
    //         String sql = String.format("SELECT '1' from %s.Z11 limit 1", lib);
    //         ResultSet rs = stmt.executeQuery(sql);
    //         while (rs.next()) {
    //             res = true;
    //         }
    //     } catch (Exception e) {
    //     }
    //     return res;
    // }
    String getAbi(String lib) {
        if (!g_abi.isEmpty()) {
            return g_abi.get(lib);
        }
        String sql = "";
        try {
            ArrayList<String> udt = new ArrayList<>();
            Connection con = _connectionService.getAS400JdbcConnection();
            Statement stmt = con.createStatement();
            sql = "SELECT distinct table_schema as LIB from qsys2.systables where table_name = 'A10' and table_schema like 'LB%UDT'";
            ResultSet rs = stmt.executeQuery(sql);
            sql = "";
            while (rs.next()) {
                String udtLib = rs.getString("LIB").trim();
                if (sql.trim().length() > 10) {
                    sql += " UNION ALL ";
                }
                sql += String.format("select '%s' as lib, substr(A10CCA, 1, 5) AS ABI FROM %s.A10 WHERE A10CAG = '99999999' AND A10TCA ='200'",
                        udtLib, udtLib);
            }
        } catch (Exception e) {
            log.error("getAbi[1]. " + e.getMessage());
        }

        try {
            Connection con = _connectionService.getAS400JdbcConnection();
            Statement stmt;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String udtLib = rs.getString("LIB").trim();
                String abi = rs.getString("ABI").trim();
                g_abi.put(udtLib, abi);
            }
        } catch (Exception e) {
            log.error("getAbi[2]. " + e.getMessage());
        }
        return g_abi.get(lib);
    }

    public Enumeration<AmbienteBanca> getAmbienti() {
        if (!g_ambienti.isEmpty()) {
            return g_ambienti.elements();
        }
        try {
            Connection con = _connectionService.getAS400JdbcConnection();
            ArrayList<String> udt = new ArrayList<>();
            Statement stmt = con.createStatement();
            String sql = "SELECT distinct table_schema as LIB from qsys2.systables where table_name = 'Z11' and table_schema like 'LB%UDT'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String lib = rs.getString("LIB").trim();
                udt.add(lib);
            }
            for (String curUdt : udt) {
                if (g_ambienti.get(curUdt) == null) {
                    String abi = getAbi(curUdt);
                    if (abi != null) {
                        AmbienteBanca b = new AmbienteBanca(curUdt, abi);
                        g_ambienti.put(b.getLibreriaDati(), b);
                    }
                }

            }
        } catch (Exception e) {
            log.error("getAmbienti[1]. " + e.getMessage());
        }
        // Leggo anche gli ambienti presenti in ZE5:
        try {
            Connection con = _connectionService.getAS400JdbcConnection();
            ArrayList<String> udt = new ArrayList<>();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM LIBFCCFG.ZE5 WHERE ZE5ASR LIKE 'HHT%'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String lib = "LB" + rs.getString("ZE5TAC").toUpperCase().trim() + rs.getString("ZE5FPT").toUpperCase().trim() + "UDT";
                udt.add(lib);
            }
            for (String curUdt : udt) {
                if (g_ambienti.get(curUdt) == null) {
                    String abi = getAbi(curUdt);
                    if (abi != null) {
                        AmbienteBanca b = new AmbienteBanca(curUdt, abi);
                        g_ambienti.put(b.getLibreriaDati(), b);
                    }
                }
            }
        } catch (Exception e) {
            log.error("getAmbienti[2]. " + e.getMessage());
        }
        return g_ambienti.elements();
    }

    public AmbienteBanca getAmbiente(String libreriaDati) {
        if (g_ambienti.get(libreriaDati) != null) {
            return g_ambienti.get(libreriaDati);
        }
        return null;
    }
}
