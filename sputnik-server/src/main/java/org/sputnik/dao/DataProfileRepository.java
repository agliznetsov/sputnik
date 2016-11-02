package org.sputnik.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;

import java.io.File;

public class DataProfileRepository extends FileRepository<DataProfile> {
    @Autowired
    Repository<DataSource> dataSourceRepository;

    public DataProfileRepository(File path) {
        super(path, DataProfile.class);
    }

    @Override
    protected void beforeDelete(DataProfile item) {
        long used = dataSourceRepository.findAll().stream().filter(it -> it.getDataProfileName().equals(item.getName())).count();
        if (used > 0) {
            throw new IllegalStateException("The profile is used by " + used + " datasource(s).");
        }
    }

    @Override
    protected void afterWrite(DataProfile item) {
        dataSourceRepository.findAll().stream().forEach(it -> {
            if (it.getDataProfileName().equals(item.getName())) {
                it.setDataProfile(item);
            }
        });
    }
}
