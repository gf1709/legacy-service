package it.allitude.legacyserviceweb.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class ISeriesProgramCallController {

    @Autowired
    private ISeriesProgramCallUtil _programCallUtil;

    @PostMapping("/call-program")
    public ProgramCallResponseDTO callProgram(@RequestBody ProgramCallRequestDTO in) throws Exception {

        return _programCallUtil.callProgram(in);
    }

    @GetMapping("/history-call-retrieve")
    public ArrayList<ProgramCallRequestDTO> retrieveHistoryCall() throws Exception {
        return _programCallUtil.retrieveHistoryCall();
    }

    @PostMapping("/history-call-save")
    public void saveHistoryCall(@RequestBody ArrayList<ProgramCallRequestDTO> calls) throws Exception {
        _programCallUtil.saveHistoryCall(calls);
    }
    @PostMapping("/show-ISY-input")
    public ArrayList<ProgramCallRequestDTO> showISYInput(@RequestBody String  aVal) throws Exception {
        return _programCallUtil.showISYInput(aVal);
    }
    @PostMapping("/show-ISY-output")
    public ArrayList<ProgramCallResponseDTO> showISYOutput(@RequestBody String  aVal) throws Exception {
        return _programCallUtil.showISYOutput(aVal);
    }
}
