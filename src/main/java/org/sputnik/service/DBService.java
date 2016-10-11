package org.sputnik.service;

import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.sputnik.model.DBDataChunk;
import org.sputnik.model.config.DataSource;

import java.io.File;

public interface DBService {
    void createDB(File dataFile, DataSource dataSource);

    void checkDB(File dataFile, DataSource dataSource);

    DBDataChunk fetchData(File dataFile, long from, long to, Integer resolution);
}
