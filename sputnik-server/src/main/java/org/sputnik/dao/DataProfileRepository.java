package org.sputnik.dao;

import org.sputnik.model.config.DataProfile;

import java.io.File;

public class DataProfileRepository extends FileRepository<DataProfile> {
    public DataProfileRepository(File path) {
        super(path, DataProfile.class);
    }
}
