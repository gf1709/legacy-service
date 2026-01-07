package it.allitude.legacyserviceweb.controllers;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.models.ISeriesSpoolUtil;
import it.allitude.legacyserviceweb.models.SpoolFileListItem;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class ISeriesSpoolController {

    @Autowired
    private ISeriesSpoolUtil _spoolUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/spool-list/{username}")
    public ArrayList<SpoolFileListItem> getSpoolList(@PathVariable String username)throws Exception {
        return _spoolUtil.getSpoolList(username);
    }

    @GetMapping("/spool-list/{jobName}/{spoolName}/{spoolNumber}")
    public ArrayList<String> getSpoolItem(@RequestHeader("Authorization") String token, @PathVariable String jobName,
            @PathVariable String spoolName, @PathVariable String spoolNumber)
            throws Exception {
        return _spoolUtil.getSpoolItem(jobName.replace("-", "/"), spoolName, spoolNumber);
    }

    @DeleteMapping("/spool-list/{jobName}/{spoolName}/{spoolNumber}")
    public void deleteSpoolItem(@RequestHeader("Authorization") String token, @PathVariable String jobName,
            @PathVariable String spoolName, @PathVariable String spoolNumber)
            throws Exception {
        _spoolUtil.deleteSpoolItem(jobName.replace("-", "/"), spoolName, spoolNumber);
    }

    @DeleteMapping("/spool-list/deleteAll/{username}")
    public void deleteSpoolItem(@PathVariable String username) throws Exception {
        _spoolUtil.deleteAllSpools(username);
    }

}
