package org.sputnik.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSerie;
import org.sputnik.model.config.DataSource;
import org.sputnik.model.config.Graph;
import org.sputnik.service.DBService;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.rrd4j.ConsolFun.AVERAGE;

@Slf4j
@Service
public class DBServiceImpl implements DBService {
    public static final int DAY = 60 * 60 * 24; //seconds in a day
    public static final int WEEK = DAY * 7; //seconds in a week
    public static final int MONTH = DAY * 31; //seconds in a month
    public static final int YEAR = DAY * 365; //seconds in a year

    @Autowired
    SputnikProperties sputnikProperties;

    @Override
    @SneakyThrows
    public void createDB(File dataFile, DataSource dataSource) {
        log.info("Creating new DB: {}", dataFile);
        RrdDef rrdDef = new RrdDef(dataFile.getPath());
        rrdDef.setStartTime(0);
        Set<String> dsNames = new HashSet<>();
        for (Graph g : dataSource.getDataProfile().getGraphs()) {
            for (DataSerie serie : g.getDataSeries()) {
                if (!dsNames.contains(serie.getName())) {
                    dsNames.add(serie.getName());
                    addDataSource(rrdDef, serie);
                }
            }
        }
        addArchives(rrdDef, AVERAGE);
        dataFile.getParentFile().mkdirs();
        new RrdDb(rrdDef).close();

    }

    private void addDataSource(RrdDef rrdDef, DataSerie serie) {
        DsType dsType = DsType.valueOf(serie.getSerieType().name());
        rrdDef.addDatasource(serie.getName(), dsType, sputnikProperties.getDataRate() * 5, 0, Double.NaN);
    }

    @Override
    @SneakyThrows
    public void checkDB(File dataFile, DataSource dataSource) {
        if (dataFile.exists()) {
            RrdDb db = new RrdDb(dataFile.getPath(), true);
            Set<DataSerie> missing;
            try {
                Set<String> dbNames = Arrays.stream(db.getRrdDef().getDsDefs()).map(DsDef::getDsName).collect(Collectors.toSet());
                missing = dataSource.getDataProfile().getGraphs().stream().flatMap(it -> it.getDataSeries().stream())
                        .filter(it -> !dbNames.contains(it.getName())).collect(Collectors.toSet());
            } finally {
                db.close();
            }
            if (!missing.isEmpty()) {
                updateDB(dataFile, missing);
            }
        }
    }

    @SneakyThrows
    private void updateDB(File src, Set<DataSerie> missing) {
        log.info("Updating existing DB: {}", src);
        File backup = findBackupName(src);
        if (!src.renameTo(backup)) {
            throw new RuntimeException("Error renaming file " + src);
        }
        RrdDb oldDb = new RrdDb(backup.getPath());
        try {
            RrdDef rrdDef = oldDb.getRrdDef();
            rrdDef.setPath(src.getPath());
            for (DataSerie ds : missing) {
                addDataSource(rrdDef, ds);
            }
            RrdDb newDb = new RrdDb(rrdDef);
            try {
                oldDb.copyStateTo(newDb);
            } finally {
                newDb.close();
            }
        } finally {
            oldDb.close();
        }

    }

    private File findBackupName(File src) {
        for(int i=0; i<1000; i++) {
            File newFile = new File(src + "." + i);
            if (!newFile.exists()) {
                return newFile;
            }
        }
        throw new RuntimeException("Failed to find a new file name for " + src);
    }

    private void addArchives(RrdDef rrdDef, ConsolFun... functions) {
        int rows = sputnikProperties.getArchiveRows();
        for (ConsolFun fun : functions) {
            rrdDef.addArchive(fun, 0.5, 1, (DAY * sputnikProperties.getDays()) / sputnikProperties.getDataRate());
            rrdDef.addArchive(fun, 0.5, WEEK / sputnikProperties.getDataRate() / rows, rows);
            rrdDef.addArchive(fun, 0.5, MONTH / sputnikProperties.getDataRate() / rows, rows);
            rrdDef.addArchive(fun, 0.5, YEAR / sputnikProperties.getDataRate() / rows, rows);
        }
    }
}
