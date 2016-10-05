package org.sputnik.service;

import org.sputnik.model.config.DataSource;

import java.io.File;

public interface DBService {
    void createDB(File dataFile, DataSource dataSource);
    void checkDB(File dataFile, DataSource dataSource);
}
