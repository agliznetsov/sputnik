package org.sputnik.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.sputnik.model.CollectStatus;
import org.sputnik.model.config.DataFormat;
import org.sputnik.model.config.DataSerie;
import org.sputnik.model.config.DataSource;
import org.sputnik.model.config.Graph;
import org.sputnik.service.CollectorService;
import org.sputnik.service.ConfigService;
import org.sputnik.service.DBService;

import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Autowired
    ObjectMapper objectMapper;

    private Map<String, Pattern> patterns = new ConcurrentHashMap<>();
    private Map<String, CollectStatus> statuses = new ConcurrentHashMap<>();

    @Override
    public synchronized void collect() {
        long start = System.currentTimeMillis();
        Collection<DataSource> dataSources = configService.getDataSources();
        int size = dataSources.size();
        AtomicInteger counter = new AtomicInteger(0);
        for (DataSource ds : dataSources) {
            if (ds.isEnabled()) {
                taskExecutor.execute(() -> {
                    CollectStatus status = new CollectStatus();
                    try {
                        collectDataSource(ds);
                        status.setOk(true);
                    } catch (Throwable e) {
                        log.error("Error collecting '" + ds.getKey() + "'", e);
                        status.setErrorMessage(e.getMessage());
                        status.setOk(false);
                    } finally {
                        status.setTime(System.currentTimeMillis() / 1000);
                        statuses.put(ds.getKey(), status);
                        int total = counter.addAndGet(1);
                        if (total == size) {
                            long end = System.currentTimeMillis();
                            log.info("Collected {} sources in {} ms", size, (end - start));
                        }
                    }
                });
            }
        }
    }

    @Override
    public CollectStatus getStatus(DataSource source) {
        return statuses.get(source.getKey());
    }

    @SneakyThrows
    private void collectDataSource(DataSource dataSource) {
        File dataFile = configService.getDataFile(dataSource);
        if (!dataFile.exists()) {
            dbService.createDB(dataFile, dataSource);
        }
        Map<Object, Object> data = collectData(dataSource);
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
        for (Map.Entry<String, Number> e : data.entrySet()) {
            list.add(e.getKey() + ": " + e.getValue());
        }
        list.sort(Comparator.naturalOrder());
        list.forEach(System.out::println);
    }

    protected long now() {
        return System.currentTimeMillis() / 1_000;
    }

    @SneakyThrows
    protected void updateDB(RrdDb rrdDb, DataSource dataSource, Map<Object, Object> data, long time) {
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

    protected Double getValue(Map<Object, Object> data, DataSerie serie) {
        if (serie.getPattern() != null) {
            Pattern pattern = getPattern(serie.getPattern());
            DoubleStream stream = data.entrySet().stream()
                    .filter(it -> pattern.matcher(it.getKey().toString()).matches())
                    .mapToDouble(it -> getDouble(it.getValue()));
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
            return getDouble(data.get(serie.getName()));
        } else {
            throw new IllegalArgumentException("Invalid serie: " + serie.toString());
        }
    }

    private Double getDouble(Object value) {
        if (value instanceof Number)
            return ((Number)value).doubleValue();
        else if (value instanceof String)
            return Double.parseDouble((String) value);
        else
            return null;
    }

    private Pattern getPattern(String regex) {
        return patterns.computeIfAbsent(regex, Pattern::compile);
    }

    private Map<Object, Object> collectData(DataSource dataSource) {
        for (DataCollector dc : dataCollectors) {
            if (dc.canHandle(dataSource)) {
                long start = System.currentTimeMillis();
                Map<Object, Object> data = convertData(dc.collectData(dataSource), dataSource.getDataFormat());
                long end = System.currentTimeMillis();
                log.info("'{}' collected in {} ms", dataSource.getKey(), (end - start));
                return data;
            }
        }
        throw new RuntimeException("No data collector found for: " + dataSource.getUrl());
    }

    @SneakyThrows
    private Map<Object, Object> convertData(String body, DataFormat dataFormat) {
        if (dataFormat == null || dataFormat == DataFormat.JSON) {
            Map<?, ?> source = objectMapper.readValue(body, Map.class);
            Map<Object, Object> target = new HashMap<>();
            flatMap(source, target, null);
            return target;
        } else if (dataFormat == DataFormat.PROPERTIES) {
            Properties properties = new Properties();
            properties.load(new StringReader(body));
            return properties;
        } else {
            throw new RuntimeException("Data format is not supported: " + dataFormat);
        }
    }

    private void flatMap(Map<?, ?> source, Map<Object, Object> target, String prefix) {
        for (Map.Entry e : source.entrySet()) {
            String key = prefix == null ? e.getKey().toString() : prefix + "." + e.getKey();
            Object value = e.getValue();
            if (value instanceof Map) {
                flatMap((Map<?, ?>) value, target, key);
            } else if (value instanceof Number || value instanceof String) {
                //filter out non-numeric values
                target.put(key, value);
            }
        }
    }

}
