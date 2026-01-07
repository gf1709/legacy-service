package it.allitude.legacyserviceweb;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.models.AppConfig;

@Component
public class StaticContextInitializer {
    private static final Logger log = LoggerFactory.getLogger(StaticContextInitializer.class);

    @Autowired
    AppConfig _cfg;

    @PostConstruct
    public void init() {
        log.info(" ");
        log.info("========================================================");
        log.info("Server name.................." + _cfg.getServerName());
        log.info("Server port.................." + _cfg.getServerPort());
        log.info("ISeries server name ........." + _cfg.getISeriesName());
        log.info("========================================================");
        log.info(" ");
    }
}
