package org.sputnik.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.sputnik.collector.DataCollector;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSerie;
import org.sputnik.model.config.DataSource;
import org.sputnik.model.config.Graph;
import org.sputnik.service.CollectorService;
import org.sputnik.service.ConfigService;
import org.sputnik.service.DBService;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;

import static org.rrd4j.ConsolFun.*;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ConfigService configService;
    @Autowired
    DBService dbService;
    @Autowired
    Collection<DataCollector> dataCollectors;
    @Autowired
    TaskExecutor taskExecutor;

    private Map<String, Pattern> patterns = new ConcurrentHashMap<>();

    @Override
    public synchronized void collect() {
        log.info("Collecting data");
        for (DataSource ds : configService.getDataSources()) {
            if (ds.isEnabled()) {
                taskExecutor.execute(() -> {
                    try {
                        collectDataSource(ds);
                    } catch (Throwable e) {
                        log.error("Data collection error", e);
                    }
                });
            }
        }
    }

    @SneakyThrows
    private void collectDataSource(DataSource dataSource) {
        File dataFile = configService.getDataFile(dataSource);
        if (!dataFile.exists()) {
            dbService.createDB(dataFile, dataSource);
        }
        Map<String, Number> data = collectData(dataSource);
//        dumpData(data);
        RrdDb rrdDb = new RrdDb(dataFile.getPath());
        try {
            updateDB(rrdDb, dataSource, data, now());
        } finally {
            rrdDb.close();
        }
    }

    private void dumpData(Map<String, Number> data) {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Number> e : data.entrySet()) {
            list.add(e.getKey() + ": " + e.getValue());
        }
        list.sort(Comparator.naturalOrder());
        list.forEach(System.out::println);
    }

    protected long now() {
        return System.currentTimeMillis() / 1_000;
    }

    @SneakyThrows
    protected void updateDB(RrdDb rrdDb, DataSource dataSource, Map<String, Number> data, long time) {
        long start = System.nanoTime();
        Set<String> dsNames = new HashSet<>();
        Sample sample = rrdDb.createSample(time);
        for (Graph g : dataSource.getDataProfile().getGraphs()) {
            for (DataSerie serie : g.getDataSeries()) {
                if (!dsNames.contains(serie.getName())) {
                    dsNames.add(serie.getName());
                    if (rrdDb.getDatasource(serie.getName()) != null) {
                        Double value = getValue(data, serie);
                        if (value != null) {
                            if (serie.getMultiplier() != null) {
                                value = value * serie.getMultiplier();
                            }
                            if ((serie.getMax() == null || serie.getMax() >= value) && (serie.getMin() == null || serie.getMin() <= value)) {
                                log.trace("Value: {} = {}", serie.getName(), value);
                                sample.setValue(serie.getName(), value);
                            } else {
                                log.debug("Value out of range: {}", value);
                            }
                        }
                    } else {
                        log.warn("Ignoring missing dataSource {} in {}. Restart sputnik to update existing DBs.", serie.getName(), rrdDb.getPath());
                    }
                }
            }
        }
        sample.update();
        long end = System.nanoTime();
        log.info("'{}' updated in {} ms", dataSource.getKey(), (end - start) / 1_000_000.0);
    }

    protected Double getValue(Map<String, Number> data, DataSerie serie) {
        if (serie.getPattern() != null) {
            Pattern pattern = getPattern(serie.getPattern());
            DoubleStream stream = data.entrySet().stream()
                    .filter(it -> pattern.matcher(it.getKey()).matches())
                    .mapToDouble(it -> it.getValue().doubleValue());
            if (serie.getAggregateFunction() != null) {
                switch (serie.getAggregateFunction()) {
                    case SUM:
                        return stream.sum();
                    case AVG:
                        OptionalDouble value = stream.average();
                        return value.isPresent() ? value.getAsDouble() : null;
                    default:
                        throw new IllegalArgumentException("unsupported aggregate func: " + serie.getAggregateFunction());
                }
            } else {
                OptionalDouble value = stream.findFirst();
                return value.isPresent() ? value.getAsDouble() : null;
            }
        } else if (serie.getName() != null) {
            Number value = data.get(serie.getName());
            return value != null ? value.doubleValue() : null;
        } else {
            throw new IllegalArgumentException("Invalid serie: " + serie.toString());
        }
    }

    private Pattern getPattern(String regex) {
        return patterns.computeIfAbsent(regex, Pattern::compile);
    }

    private Map<String, Number> collectData(DataSource dataSource) {
        for (DataCollector dc : dataCollectors) {
            if (dc.canHandle(dataSource)) {
                long start = System.currentTimeMillis();
                Map<String, Number> data = dc.collectData(dataSource);
                long end = System.currentTimeMillis();
                log.info("'{}' collected in {} ms", dataSource.getKey(), (end - start));
                return data;
            }
        }
        throw new RuntimeException("No data collector found for: " + dataSource.getUrl());
    }

}
