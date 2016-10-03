package org.sputnik.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.sputnik.config.SputnikProperties;
import org.sputnik.context.SmartLifecycleBean;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ConfigServiceImpl extends SmartLifecycleBean implements ConfigService {

    private static final String DATA_PROFILES_DIR = "config/profiles";
    private static final String DATA_SOURCES_DIR = "config/sources";

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ObjectMapper objectMapper;
    
    private volatile Map<String, DataProfile> dataProfiles;
    private volatile Map<String, DataSource> dataSources;

    @Override
    public synchronized void refresh() {
        log.info("Reading configuration from: {}", sputnikProperties.getHomeDirectory());
        dataProfiles = readFiles(DATA_PROFILES_DIR, DataProfile.class);
        dataSources = readFiles(DATA_SOURCES_DIR, DataSource.class);
        dataSources.values().forEach(this::resolveProfile);
    }

    @Override
    public Collection<DataProfile> getDataProfiles() {
        return dataProfiles.values();
    }

    @Override
    public DataProfile getDataProfile(String name) {
        return notNull(dataProfiles.get(name));
    }

    @Override
    public void saveDataProfile(DataProfile dataProfile) {
        write(DATA_PROFILES_DIR, dataProfile.getName(), dataProfile);
        dataProfiles.put(dataProfile.getName(), dataProfile);
    }

    @Override
    public Collection<DataSource> getDataSources() {
        return dataSources.values();
    }

    @Override
    public DataSource getDataSource(String name) {
        return notNull(dataSources.get(name));
    }

    @Override
    public void saveDataSource(DataSource dataSource) {
        resolveProfile(dataSource);
        write(DATA_SOURCES_DIR, dataSource.getName(), dataSource);
        dataSources.put(dataSource.getName(), dataSource);
    }

//    private //

    private void resolveProfile(DataSource dataSource) {
        dataSource.setDataProfile(getDataProfile(dataSource.getDataProfileName()));
    }

    private <T> T notNull(T src) {
        Assert.notNull(src);
        return src;
    }


    private <T> Map<String, T> readFiles(String directory, Class<T> clazz) {
        File dir = new File(sputnikProperties.getHomeDirectory(), directory);
        dir.mkdirs();

        Map<String, T> map = new HashMap<>();
        for(File file : dir.listFiles()) {
            T object = readFile(file, clazz);
            if (object != null) {
                map.put(object.toString(), object);
            }
        }
        return map;
    }


    private <T> T readFile(File file, Class<T> clazz) {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error("Invalid config file " + file.getName(), e);
            return null;
        }
    }

    @SneakyThrows
    private void write(String dir, String name, Object object) {
        objectMapper.writeValue(new File(new File(sputnikProperties.getHomeDirectory(), dir), name), object);
    }

    @Override
    protected void doStart() {
        refresh();
    }

    @Override
    protected void doStop() {

    }
}
