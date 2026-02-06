package it.allitude.legacyserviceweb.controllers;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import org.json.JSONWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.CreateCdcTableDdlDTO;
import it.allitude.legacyserviceweb.DTOs.DspLogRequestDTO;
import it.allitude.legacyserviceweb.DTOs.IFSListFileRequestDTO;
import it.allitude.legacyserviceweb.DTOs.IFSListFileResponseDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.models.ISeriesGPLUtil;
import it.allitude.legacyserviceweb.models.ISeriesIFSUtil;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/api"})

public class ISeriesUtilityController {

    @Autowired
    ISeriesGPLUtil _iSeriesGPLUtil;
    @Autowired
    ISeriesIFSUtil _iSeriesIFSUtil;
    @Autowired
    ConnectionService _connectionService;

    @PostMapping("/utility/create-cdc-table-DDL")
    public ArrayList<String> createCDCTableDDL(@RequestBody CreateCdcTableDdlDTO in) throws Exception {
        ArrayList<String> res = new ArrayList<>();
        return _iSeriesGPLUtil.createCDCTableDDL(in.getLibrary(), in.getFile());
    }

    @PostMapping("/utility/listFiles")
    public IFSListFileResponseDTO listFiles(@RequestBody IFSListFileRequestDTO in) throws Exception {
        return _iSeriesIFSUtil.listFiles(in.getDirectory(), in.getPattern());
    }
    @PostMapping("/utility/splitIFSFileContent")
    public boolean splitIFSFileContent(@RequestBody String aFileName) throws Exception {
        return _iSeriesIFSUtil.split(aFileName);
    }
    @PostMapping("/utility/getIFSFileContent")
    public ArrayList<String> getIFSFileContent(@RequestBody String aFileName) throws Exception {
        return _iSeriesIFSUtil.getIFSFileContent(aFileName);
    }
    @PostMapping(value="/utility/getIFSFileContentZipped")
    public ArrayList<String> getIFSFileContentZipped(@RequestBody String aFileName) throws Exception {
        return _iSeriesIFSUtil.getIFSFileContentZipped(aFileName);
    }

    @PostMapping("/utility/getIFSFilesContent")
    public ArrayList<String> getIFSFilesContent(@RequestBody ArrayList<String> aFileNames) throws Exception {
        return _iSeriesIFSUtil.getIFSFilesContent(aFileNames);
    }
    @PostMapping("/utility/deleteIFSFile")
    public boolean deleteIFSFile(@RequestBody String aFileName) throws Exception {
        return _iSeriesIFSUtil.deleteIFSFile(aFileName);
    }
    @PostMapping("/utility/findSibankCall")
    public ArrayList<String> findSibankCall(@RequestBody ArrayList<String> aFileNames) throws Exception {
        return _iSeriesIFSUtil.findSibankCall(aFileNames);
    }
    @PostMapping("/utility/deleteIFSFiles")
    public boolean deleteIFSFile(@RequestBody ArrayList<String> aFileNames) throws Exception {
        return _iSeriesIFSUtil.deleteIFSFiles(aFileNames);
    }

    @PostMapping("/dsplog")
    public ResponseEntity<?> dsplog(@RequestBody DspLogRequestDTO in) {
        int maxRows = 500;
        try {
            Connection con = _connectionService.getAS400JdbcConnection();
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
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;            
        }
    }
}
