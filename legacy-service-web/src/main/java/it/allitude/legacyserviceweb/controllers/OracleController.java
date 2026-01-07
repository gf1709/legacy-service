package it.allitude.legacyserviceweb.controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.OpenResultsetRequestDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.models.JSession;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/api"})

public class OracleController {

    static final String g_sqlHistoryDirectory = "run_time_resources/static/sql_history";

    @Autowired
    ConnectionService _connectionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String getSqlHistoryFileName() {
        String res = String.format("%s_%s.txt", JSession.getCurrentSession().getUser().toUpperCase(),
                JSession.getCurrentSession().getTerminal().toUpperCase());
        return res;
    }

    @PostMapping("/open-resultset")
    public ResponseEntity<?> openResulset(@RequestBody OpenResultsetRequestDTO in) {
        int maxRows = Math.min(300, in.getRecno());

        try {
            Connection con = _connectionService.getOracleS2AConnection();
            ResultSet rs = con.createStatement().executeQuery(in.getSql());
            ResultSetMetaData rsmd = rs.getMetaData();
            int nrRows = 0;
            StringWriter sb = new StringWriter();
            JSONWriter writer = new JSONWriter(sb);
            writer.array();
            while (rs.next()) {
                nrRows += 1;
                if (nrRows > maxRows) {
                    break;
                }
                writer.object();
                for (int idx = 1; idx <= rsmd.getColumnCount(); idx++) {
                    writer.key(rsmd.getColumnName(idx)); // write key:value pairs
                    writer.value(rs.getObject(idx));
                }
                writer.endObject();
            }
            rs.close();
            con.close();
            writer.endArray();
            String result = sb.toString();
            saveSql(in.getSql());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity<?> resEnt = new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
            // return ResponseEntity.ok("aa");
        }
    }

    void saveSql(String anSql) throws java.io.IOException, ClassNotFoundException {
        Path uploadPath = Paths.get(g_sqlHistoryDirectory);
        Path filePath = uploadPath.resolve(getSqlHistoryFileName());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        boolean historyContainsElement = false;
        ArrayList<String> history = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(filePath.toAbsolutePath().toString());
            ObjectInputStream ois = new ObjectInputStream(fis);
            history = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();

            for (String ele : history) {
                if (ele.equals(anSql)) {
                    historyContainsElement = true;
                }
            }
            if (historyContainsElement) {
                return;
            }
        } catch (Exception e) {
        }

        if (history.size() > 60) {
            history = (ArrayList<String>) history.subList(history.size() - 60, history.size());
        }
        if (!historyContainsElement) {
            history.add(anSql);
        }
        FileOutputStream fos = new FileOutputStream(filePath.toAbsolutePath().toString());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(history);
        oos.close();
        fos.close();
    }

    @GetMapping("/history-sql-retrieve")
    public ArrayList<String> retrieveSqlHistory() {
        ArrayList<String> history = new ArrayList<String>();
        Path uploadPath = Paths.get(g_sqlHistoryDirectory);
        Path filePath = uploadPath.resolve(getSqlHistoryFileName());
        try {
            FileInputStream fis = new FileInputStream(filePath.toAbsolutePath().toString());
            ObjectInputStream ois = new ObjectInputStream(fis);
            history = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
        }
        return history;
    }

    @GetMapping("/update_legacy_terminal_abi_mapper")
    public void updateLegacyTerminalAbiMapper() throws Exception {
        Connection con = _connectionService.getOracleMSSConnection();
        CallableStatement cStmt = con.prepareCall("call MSS_CDC.UTILITY_A10.EXPORT_LEGACY_ABI_INFO_TO_MSS_LEGAL_ENTITY_MAP_A10()");
        cStmt.execute();
    }

}
