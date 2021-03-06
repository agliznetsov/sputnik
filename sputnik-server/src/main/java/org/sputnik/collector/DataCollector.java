package org.sputnik.collector;

import org.sputnik.model.config.DataSource;

import java.util.Map;

public interface DataCollector {
    boolean canHandle(DataSource dataSource);
    String collectData(DataSource dataSource);
}
