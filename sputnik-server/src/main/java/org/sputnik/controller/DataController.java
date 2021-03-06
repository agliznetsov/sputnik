package org.sputnik.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.dao.Repository;
import org.sputnik.model.DBDataChunk;
import org.sputnik.model.DataReport;
import org.sputnik.model.config.DataSerie;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.CollectorService;
import org.sputnik.service.DBService;
import org.sputnik.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DataController {

    @Autowired
    Repository<DataSource> dataSourceRepository;
    @Autowired
    DBService dbService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    CollectorService collectorService;
    @Autowired
    HttpServletRequest request;

    @RequestMapping("/now")
    public long now() {
        return System.currentTimeMillis() / 1000;
    }

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public void collect() {
        log.debug("collect");
        SecurityUtils.getUser(request);
        taskScheduler.schedule(() -> collectorService.collect(), new Date());
    }

    @RequestMapping("/data/{group}/{name}")
    public DataReport getData(@PathVariable("group") String group,
                              @PathVariable("name") String name,
                              @RequestParam(value = "start", required = false) Long from,
                              @RequestParam(value = "end", required = false) Long to,
                              @RequestParam(value = "resolution", required = false) Integer resolution) {
        if (to == null) {
            to = now();
        }
        if (from == null) {
            from = to - 60 * 60; //last hour by default
        }

        Optional<DataSource> ds = dataSourceRepository.findAll().stream().filter(it -> it.getGroupName().equals(group) && it.getName().equals(name)).findFirst();
        if (ds.isPresent()) {
            File dataFile = dbService.getDataFile(ds.get());
            DataReport report = new DataReport();
            report.setDataProfile(ds.get().getDataProfile());
            report.setFrom(from);
            report.setTo(to);
            if (dataFile.exists()) {
                DBDataChunk chunk = dbService.fetchData(dataFile, from, to, resolution);
                report.setTimestamps(chunk.timestamps);
                report.setLastUpdate(chunk.lastUpdate);
                Set<String> names = report.getDataProfile().getGraphs().stream().flatMap(it -> it.getDataSeries().stream()).map(DataSerie::getName).collect(Collectors.toSet());
                for (int i = 0; i < chunk.dsNames.length; i++) {
                    String dsName = chunk.dsNames[i];
                    if (names.contains(dsName)) {
                        report.getValues().put(dsName, chunk.values[i]);
                    }
                }
            }
            return report;
        } else {
            throw new IllegalArgumentException("Invalid data source");
        }
    }

}
