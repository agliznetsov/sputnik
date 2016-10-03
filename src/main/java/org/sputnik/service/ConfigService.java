package org.sputnik.service;

import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;

import java.util.Collection;
import java.util.List;

public interface ConfigService {
    
    void refresh();
    
    Collection<DataProfile> getDataProfiles();

    DataProfile getDataProfile(String name);
    
    void saveDataProfile(DataProfile dataProfile);

    Collection<DataSource> getDataSources();

    DataSource getDataSource(String name);

    void saveDataSource(DataSource dataSource);
    
}
