package org.sputnik.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.endpoint.ConfigurationPropertiesReportEndpoint;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.CollectorService;
import org.sputnik.service.ConfigService;
import org.sputnik.service.DBService;
import org.sputnik.util.MapUtils;

import java.io.File;
import java.util.Map;

@Slf4j
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
    @Autowired
    ConfigurationPropertiesReportEndpoint configurationPropertiesReportEndpoint;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Launching sputnik");
        printConfiguration();
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

    private void printConfiguration() {
        log.info("Configuration properties:");
        Map<String, Object> map = configurationPropertiesReportEndpoint.invoke();
        String prefix = MapUtils.getPath(map, "sputnikProperties.prefix");
        Map<Object, Object> properties = MapUtils.getPath(map, "sputnikProperties.properties");
        for(Map.Entry e : properties.entrySet()) {
            log.info(prefix + "." + e.getKey() + " = " + e.getValue());
        }
    }

}
