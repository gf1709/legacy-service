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
import it.allitude.legacyserviceweb.models.LibraryListItem;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class ISeriesObjectController {

    @Autowired
    private ISeriesObjectUtil _objectUtil;

    @Autowired
    private ISeriesSourceUtil _sourceUtil;
    

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/ffd")
    public ResponseEntity<?> getFFD(@RequestBody FFDRequestDTO ffdInfo) throws Exception {
        FFDResponseDTO responseDTO = _objectUtil.getFFD(ffdInfo.getLibrary(), ffdInfo.getDdsName());
        if (responseDTO != null && responseDTO.getFields() != null && responseDTO.getFields().size() > 0 && responseDTO.getIdf() != null && responseDTO.getIdf().length() > 0)
            return ResponseEntity.ok(responseDTO);
        else
            return ResponseEntity.notFound().build();
    }

    @PostMapping("/wrkobj")
    public List<WRKOBJResponseDTO> wrkobj(@RequestBody WRKOBKRequestDTO wrkobjInfo) throws Exception {
        List<WRKOBJResponseDTO> res = _objectUtil.wrkobj(wrkobjInfo);
        logger.info("wrkobj done");
        return res;
    }

    @PostMapping("/dspobjd")
    public DSPOBJDResponseDTO dspobjd(@RequestHeader("Authorization") String token,
            @RequestBody DSPOBJDRequestDTO dspobjdInfo) throws Exception {
        DSPOBJDResponseDTO res = _objectUtil.dspobjd(dspobjdInfo);
        logger.info("dspobjd done");
        return res;
    }

    @PostMapping("/get-source-list")
    public GetSourceListResponseDTO getSourceList(@RequestBody GetSourceListRequestDTO getSourceInfo)
            throws Exception {
        ArrayList<GetSourceListResponseItem> list = _sourceUtil.getSourceList(getSourceInfo.getLibrary(),
                getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember());
        GetSourceListResponseDTO res = new GetSourceListResponseDTO(list);
        return res;
    }

    @PostMapping("/get-source")
    public GetSourceResponseDTO getSource(@RequestBody GetSourceRequestDTO getSourceInfo)
            throws Exception {
        ArrayList<String> lines = _sourceUtil.getSource(getSourceInfo.getLibrary(),
                getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember(), getSourceInfo.isExplodeCOPY());
        GetSourceResponseDTO res = new GetSourceResponseDTO(getSourceInfo.getLibrary(),
                getSourceInfo.getSourceFile(), getSourceInfo.getSourceMember(), getSourceInfo.isExplodeCOPY(), lines);
        return res;
    }

    @GetMapping("/library-list")
    public ArrayList<LibraryListItem> getLibraryList()
            throws Exception {
        return _objectUtil.GetLibraryList();
    }

    @PutMapping("/library-list/{library}")
    public void addLibraryToLibraryList(@PathVariable String library)
            throws Exception {
        _objectUtil.AddLibraryToLibraryList(library);
    }

    @DeleteMapping("/library-list/{library}")
    public void removeLibraryFromLibraryList(@PathVariable String library)
            throws Exception {
        _objectUtil.RemoveLibraryFromLibraryList(library);
    }
}
