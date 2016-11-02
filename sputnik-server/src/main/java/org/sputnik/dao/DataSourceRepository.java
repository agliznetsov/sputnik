package org.sputnik.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;

import java.io.File;

public class DataSourceRepository extends FileRepository<DataSource> {
    @Autowired
    Repository<DataProfile> dataProfileRepository;

    public DataSourceRepository(File path) {
        super(path, DataSource.class);
    }

    @Override
    protected void afterRead(DataSource item) {
        resolveProfile(item);
    }

    @Override
    protected void beforeWrite(DataSource item) {
        resolveProfile(item);
    }

    private void resolveProfile(DataSource item) {
        DataProfile profile = dataProfileRepository.findAll().stream()
                .filter(it -> it.getName().equals(item.getDataProfileName())).findFirst().get();
        item.setDataProfile(profile);
    }
}
