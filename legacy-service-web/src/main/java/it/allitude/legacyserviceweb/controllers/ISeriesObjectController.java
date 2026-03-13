package it.allitude.legacyserviceweb.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.DSPOBJDRequestDTO;
import it.allitude.legacyserviceweb.DTOs.DSPOBJDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.FFDRequestDTO;
import it.allitude.legacyserviceweb.DTOs.FFDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.GetSourceListRequestDTO;
import it.allitude.legacyserviceweb.DTOs.GetSourceListResponseDTO;
import it.allitude.legacyserviceweb.DTOs.GetSourceListResponseItem;
import it.allitude.legacyserviceweb.DTOs.GetSourceRequestDTO;
import it.allitude.legacyserviceweb.DTOs.GetSourceResponseDTO;
import it.allitude.legacyserviceweb.DTOs.WRKOBJResponseDTO;
import it.allitude.legacyserviceweb.DTOs.WRKOBKRequestDTO;
import it.allitude.legacyserviceweb.models.ISeriesObjectUtil;
import it.allitude.legacyserviceweb.models.ISeriesSourceUtil;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/api"})

public class ISeriesObjectController {

    @Autowired
    private ISeriesObjectUtil _objectUtil;

    @Autowired
    private ISeriesSourceUtil _sourceUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/ffd")
    public ResponseEntity<?> getFFD(@RequestBody FFDRequestDTO ffdInfo) throws Exception {
        FFDResponseDTO responseDTO = _objectUtil.getFFD(ffdInfo.getLibrary(), ffdInfo.getDdsName());
        if (responseDTO != null && responseDTO.getFields() != null && responseDTO.getFields().size() > 0 && responseDTO.getIdf() != null && responseDTO.getIdf().length() > 0) {
            return ResponseEntity.ok(responseDTO); 
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/wrkobj")
    public ResponseEntity<?> wrkobj(@RequestBody WRKOBKRequestDTO wrkobjInfo) {
        try {
            List<WRKOBJResponseDTO> res = _objectUtil.wrkobj(wrkobjInfo);
            logger.info("wrkobj done");
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/dspobjd")
    public ResponseEntity<?> dspobjd(@RequestHeader("Authorization") String token, @RequestBody DSPOBJDRequestDTO dspobjdInfo) {
        try {
            DSPOBJDResponseDTO res = _objectUtil.dspobjd(dspobjdInfo);
            logger.info("dspobjd done");
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/get-source-list")
    public ResponseEntity<?> getSourceList(@RequestBody GetSourceListRequestDTO getSourceInfo) {
        try {
            ArrayList<GetSourceListResponseItem> list = _sourceUtil.getSourceList(getSourceInfo.getLibrary(), getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember());
            GetSourceListResponseDTO res = new GetSourceListResponseDTO(list);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }

    @PostMapping("/get-source")
    public ResponseEntity<?> getSource(@RequestBody GetSourceRequestDTO getSourceInfo) {
        try {
            ArrayList<String> lines = _sourceUtil.getSource(getSourceInfo.getLibrary(),
                    getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember(), getSourceInfo.isExplodeCOPY());
            GetSourceResponseDTO res = new GetSourceResponseDTO(getSourceInfo.getLibrary(),
                    getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember(), getSourceInfo.isExplodeCOPY(), lines);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }

    @GetMapping("/library-list")
    public ResponseEntity<?> getLibraryList() {
        try {
            logger.info("Getting library list");
            return ResponseEntity.ok(_objectUtil.GetLibraryList());
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }

    @PutMapping("/library-list/{library}")
    public ResponseEntity<?> addLibraryToLibraryList(@PathVariable String library) {
        try {
            _objectUtil.AddLibraryToLibraryList(library);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }

    @DeleteMapping("/library-list/{library}")
    public ResponseEntity<?> removeLibraryFromLibraryList(@PathVariable String library) {
        try {
            _objectUtil.RemoveLibraryFromLibraryList(library);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }

}
