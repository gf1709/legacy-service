package it.allitude.legacyserviceweb.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.ProgramCallRequestDTO;
import it.allitude.legacyserviceweb.DTOs.ProgramCallResponseDTO;
import it.allitude.legacyserviceweb.models.ISeriesProgramCallUtil;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/api"})

public class ISeriesProgramCallController {

    @Autowired
    private ISeriesProgramCallUtil _programCallUtil;

    @PostMapping("/call-program")
    public ResponseEntity<?> callProgram(@RequestBody ProgramCallRequestDTO in) {
        ProgramCallResponseDTO res;
        try {
            res = _programCallUtil.callProgram(in);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/history-call-retrieve")
    public ResponseEntity<?> retrieveHistoryCall() {
        ArrayList<ProgramCallRequestDTO> res;
        try {
            res = _programCallUtil.retrieveHistoryCall();
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/history-call-save")
    public void saveHistoryCall(@RequestBody ArrayList<ProgramCallRequestDTO> calls) {
        try {
            _programCallUtil.saveHistoryCall(calls);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }
        return;
    }

    @PostMapping("/show-ISY-input")
    public ResponseEntity<?> showISYInput(@RequestBody String aVal) {
        ArrayList<ProgramCallRequestDTO> res;
        try {
            res = _programCallUtil.showISYInput(aVal);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/show-ISY-output")
    public ResponseEntity<?> showISYOutput(@RequestBody String aVal) {
        ArrayList<ProgramCallResponseDTO> res;
        try {
            res = _programCallUtil.showISYOutput(aVal);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
        return ResponseEntity.ok(res);
    }
}
