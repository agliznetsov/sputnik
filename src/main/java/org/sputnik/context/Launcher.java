package org.sputnik.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.CollectorService;
import org.sputnik.service.ConfigService;
import org.sputnik.service.DBService;

import java.io.File;

@Slf4j
@Component
public class Launcher implements ApplicationRunner {

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ConfigService configService;
    @Autowired
    DBService dbService;
    @Autowired
    CollectorService collectorService;
    @Autowired
    TaskScheduler taskScheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Launching sputnik");
        configService.refresh();
        for (DataSource dataSource : configService.getDataSources()) {
            File dataFile = configService.getDataFile(dataSource);
            if (dataFile.exists()) {
                dbService.checkDB(dataFile, dataSource);
            } else {
                dbService.createDB(dataFile, dataSource);
            }
        }
        taskScheduler.scheduleAtFixedRate(() -> collectorService.collect(), sputnikProperties.getDataRate() * 1_000);
    }

}
