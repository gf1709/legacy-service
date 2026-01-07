package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesGPLUtil {

    static class colDef {
        String name;
        String type;
        int len;
        int scale;
        String comment;

        public String getName() {
            return name.trim().toUpperCase();
        }
        public String getType() {
            return type.toUpperCase().trim();
        }
        public int getLen() {
            return len;
        }
        public int getScale() {
            return scale;
        }
        public String getComment() {
            return comment.trim();
        }
        public colDef(String name, String type, int len, int scale, String comment) {
            this.name = name;
            this.type = type;
            this.len = len;
            this.scale = scale;
            this.comment = comment;
        }
        String getOracleDefinition()
        {
            String res = "";
            if (type.equals("CHAR"))
                res = String.format("\"%s\" VARCHAR2(%d CHAR)", name, len);
            else if (type.equals("DATE"))
                res = String.format("\"%s\" DATE", name);
            else if (type.equals("NUMERIC") || type.equals("DECIMAL"))
                res = String.format("\"%s\" NUMBER", name);
            else 
                res = String.format("\"%s\" todo greg!!!", name);
            return res;
        }
    }

    private final ConnectionService _connectionService;

    public ISeriesGPLUtil(ConnectionService anISeriesConnectionService) {
        this._connectionService = anISeriesConnectionService;
    }

    public ArrayList<String> createCDCTableDDL(String libName, String fileName) throws Exception {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<colDef> cols = new ArrayList<colDef>();

        Connection con = _connectionService.getAS400JdbcConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(
                "SELECT COLUMN_NAME AS COLNAME,DATA_TYPE AS COLTYPE,LENGTH AS COLLEN, NUMERIC_SCALE AS COLSCALE, COLUMN_HEADING AS COLCOMMENT FROM QSYS2.SYSCOLUMNS WHERE TABLE_SCHEMA='%s' AND TABLE_NAME = '%s' ORDER BY ORDINAL_POSITION",
                libName, fileName));

        while (rs.next()) {
            colDef c = new colDef(
                    rs.getString("COLNAME"),
                    rs.getString("COLTYPE"),
                    rs.getInt("COLLEN"),
                    rs.getInt("COLSCALE"),
                    rs.getString("COLCOMMENT"));
            cols.add(c);
        }
        rs.close();
        res.add("------------------------------------------------------------------------------------------------------------------------");
        res.add("--- Modifiche - Inizio");
        res.add("------------------------------------------------------------------------------------------------------------------------");
        res.add( String.format("BEGIN EXECUTE IMMEDIATE 'DROP TABLE %s_T CASCADE CONSTRAINTS PURGE' ; EXCEPTION WHEN OTHERS THEN NULL; END;", fileName));
        res.add("/");
        res.add(" ");

        res.add(String.format("CREATE TABLE %s_T (", fileName));
        res.add("    CDC_ABI NUMBER NOT NULL,");
        res.add("    CDC_RRN NUMBER         ,");
        for (int index = 0; index < cols.size(); index++) {
            res.add(String.format("    %s," ,cols.get(index).getOracleDefinition()));
        }
        res.add("    CDC_SYS VARCHAR2(64 CHAR),");
        res.add("    CDC_LIB VARCHAR2(64 CHAR),");
        res.add("    CDC_ENTTYP VARCHAR2(64 CHAR),");
        res.add("    CDC_TS VARCHAR2(64 CHAR),");
        res.add("    CDC_USER VARCHAR2(64 CHAR)");
        res.add(") ROWDEPENDENCIES;");      
        res.add(" ");
        res.add(" ");
        
        res.add(String.format("CREATE INDEX %s_T_IDX01   ON %s_T (CDC_ABI, CDC_RRN);", fileName, fileName));
        res.add(String.format("CREATE INDEX %s_T_CDC_IDX ON %s_T (CDC_ABI, TODO GREG!!!,TODO GREG!!!!);", fileName, fileName));
        res.add(" ");
        res.add(" ");

        res.add(String.format("COMMENT ON TABLE %s_T  IS 'TODO GREG';", fileName));
        for (int index = 0; index < cols.size(); index++) {
            res.add(String.format("COMMENT ON COLUMN \"%s_T\".\"%s\" IS '%s';", fileName, cols.get(index).getName(),  cols.get(index).getComment()));
        }
        res.add(" ");
        res.add(" ");

        res.add(String.format("GRANT ALL PRIVILEGES ON %s_T TO KAFKACONNC;", fileName));
        // res.add(String.format("GRANT SELECT ON %s_T TO MS_ALL_READ_ROLE;", fileName));
        // res.add(String.format("GRANT ALTER ON  %s_T TO MS_ALL_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT DELETE ON %s_T TO MS_ALL_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT INSERT ON %s_T TO MS_ALL_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT UPDATE ON %s_T TO MS_ALL_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT SELECT ON %s_T TO MSS_CDC_READ_ROLE;", fileName));
        // res.add(String.format("GRANT DELETE ON %s_T TO MSS_CDC_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT INSERT ON %s_T TO MSS_CDC_WRITE_ROLE;", fileName));
        // res.add(String.format("GRANT UPDATE ON %s_T TO MSS_CDC_WRITE_ROLE;", fileName));

        res.add(" ");
        res.add("-- grant necessarie per utilizzate lo kafkamanager");
        // res.add(String.format("GRANT SELECT ON %s_T TO MSS_CDC;", fileName));
        // res.add(String.format("GRANT DELETE ON %s_T TO MSS_CDC;", fileName));
        // res.add(String.format("GRANT INSERT ON %s_T TO MSS_CDC;", fileName));
        // res.add(String.format("GRANT UPDATE ON %s_T TO MSS_CDC;", fileName));

        res.add(String.format("GRANT SELECT ON %s_T TO MSS_CDC_AP;", fileName));
        res.add(String.format("GRANT DELETE ON %s_T TO MSS_CDC_AP;", fileName));
        res.add(String.format("GRANT INSERT ON %s_T TO MSS_CDC_AP;", fileName));
        res.add(String.format("GRANT UPDATE ON %s_T TO MSS_CDC_AP;", fileName));


        res.add(" ");
        res.add(" ");

        res.add(String.format("CREATE OR REPLACE SYNONYM %s FOR %s_T;", fileName, fileName));
        res.add(" ");

        res.add(String.format("CREATE OR REPLACE VIEW %s_CDC AS (", fileName));
        res.add("SELECT CDC_ABI, ");
        res.add("     CDC_RRN, ");
        res.add("     CDC_SYS, ");
        res.add("     CDC_LIB, ");
        res.add("     CDC_ENTTYP, ");
        res.add("     TO_TIMESTAMP(REPLACE(SUBSTR(CDC_TS, 1, 26), 'T', ' '), 'YYYY-MM-DD HH24.MI.SS.FF') CDC_TS, ");
        res.add("     SCN_TO_TIMESTAMP(ORA_ROWSCN) ORA_TS, ");
        res.add(String.format("     SCN_TO_TIMESTAMP(ORA_ROWSCN) - TO_TIMESTAMP(REPLACE(SUBSTR(CDC_TS, 1, 26), 'T', ' '), 'YYYY-MM-DD HH24.MI.SS.FF') ELAPSED FROM %s_T ", fileName));
        res.add(");");

        res.add("COMMIT;");
        res.add(" ");

        return res;

    }

}

