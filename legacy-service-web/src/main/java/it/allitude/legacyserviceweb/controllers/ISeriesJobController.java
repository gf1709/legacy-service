package it.allitude.legacyserviceweb.controllers;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.JobDetailInfoRequestDTO;
import it.allitude.legacyserviceweb.DTOs.JobDetailInfoResponseDTO;
import it.allitude.legacyserviceweb.DTOs.JobInfoRequestDTO;
import it.allitude.legacyserviceweb.DTOs.JobListResponseDTO;
import it.allitude.legacyserviceweb.DTOs.NetStatJobInfoRequestDTO;
import it.allitude.legacyserviceweb.DTOs.ServiceInfoResponseDTO;
import it.allitude.legacyserviceweb.DTOs.WRKACTJOBRequestDTO;
import it.allitude.legacyserviceweb.models.ISeriesJobUtil;
import it.allitude.legacyserviceweb.models.JSession;
import it.allitude.legacyserviceweb.models.WRKACTJOB_Filter;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/api"})

public class ISeriesJobController {

    @Autowired
    ISeriesJobUtil _jobUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/netstat_job_info")
    public ResponseEntity<?> netstat_job_info(@RequestBody NetStatJobInfoRequestDTO in)  {
        ISeriesJobUtil util = _jobUtil;
        try {
             ArrayList<JobListResponseDTO> res = util.netstat_job_info(in.getPort(), in.getUserName(), in.getJobName());
             return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @GetMapping("/socket_service_info")
    public ResponseEntity<?> getSocketServiceInfo() {
        ISeriesJobUtil util = _jobUtil;
        try {
            ArrayList<ServiceInfoResponseDTO> res = util.getSocketServiceInfo();
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/wrkactjob")
    public ResponseEntity<?> getActiveJobs(@RequestBody WRKACTJOBRequestDTO in) {
        ISeriesJobUtil util = _jobUtil;
        WRKACTJOB_Filter filter = new WRKACTJOB_Filter();
        filter.setJobName(in.getJobName());
        filter.setUserName(in.getUserName());
        filter.setSortByJobName(in.isSortByJobName());
        filter.setSortByJobStatus(in.isSortByJobStatus());
        try {
            ArrayList<JobListResponseDTO> res = util.WRKACTJOB(filter);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession() {
        try {
            JSession session = JSession.getCurrentSession();
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }           
    }

    @PostMapping("/endjob")
    public ResponseEntity<?> endJob(@RequestBody JobInfoRequestDTO in) {
        try {
            ISeriesJobUtil util = _jobUtil;
            util.endJob(in.getJobName(), in.getUserName(), in.getJobNumber());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/getjoblog")
    public ResponseEntity<?> getJobLog(@RequestBody ArrayList<JobInfoRequestDTO> in) {

        ISeriesJobUtil util = _jobUtil;
        if (in.size() == 1) {
            try {
                return ResponseEntity.ok(util.getJobLog(in.get(0).getJobName(), in.get(0).getUserName(), in.get(0).getJobNumber()));
            } catch (Exception ex) {
                ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
                return resEnt;
            }
        }

        try {
            ArrayList<String> res = new ArrayList<>();
            for (int i = 0; i < in.size(); i++) {
                // pongo il limite a 100000 righe
                if (res.size() > 100000) {
                    res.add("ATTENZIONE: log troncato. Raggiunto massimo numero di righe,");
                    res.add("E' stato raggiunto il limite di 100.000 righe. Ridurre il numero di job o di righe di log.");
                    break;
                }
                JobInfoRequestDTO job = in.get(i);
                ArrayList<String> resSingle = util.getJobLog(job.getJobName(), job.getUserName(), job.getJobNumber());
                for (String li : resSingle) {
                    res.add("[" + job.getJobName() + "." + job.getUserName() + "." + job.getJobNumber() + "] " + li);
                }
            }
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/set-joblog-verbose")
    public ResponseEntity<?> setJobLogVerbose(@RequestBody JobInfoRequestDTO in) {

        try {
             ISeriesJobUtil util = _jobUtil;
             util.setJobLogVerbose(in.getJobName(), in.getUserName(), in.getJobNumber());
             return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ResponseEntity<?> resEnt = new ResponseEntity<>(ex.toString(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            return resEnt;
        }
    }

    @PostMapping("/getjobdetail")
    public ResponseEntity<JobDetailInfoResponseDTO> getJobDetail(@RequestBody JobDetailInfoRequestDTO in) {

        ISeriesJobUtil util = _jobUtil;
        return util.getJobDetail(in);
    }
}
