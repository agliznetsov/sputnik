package org.sputnik.service;

import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface ConfigService {
    
    Collection<DataProfile> getDataProfiles();

    DataProfile getDataProfile(String name);
    
    void saveDataProfile(DataProfile dataProfile);

    Collection<DataSource> getDataSources();

    void saveDataSource(DataSource dataSource);

    File getDataFile(DataSource dataSource);

    void deleteDataSource(String id);
}
