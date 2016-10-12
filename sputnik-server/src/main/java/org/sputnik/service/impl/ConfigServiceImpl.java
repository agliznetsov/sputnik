package org.sputnik.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService, InitializingBean {

    private static final String PROFILES_DIR = "config/profiles";
    private static final String SOURCES_PATH = "config/sources";
    private static final String DATA_DIR = "data";

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ObjectMapper objectMapper;

    private File sourcesFile;

    @Override
    public void afterPropertiesSet() throws Exception {
        new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR).mkdirs();
        new File(sputnikProperties.getHomeDirectory(), DATA_DIR).mkdirs();
        sourcesFile = new File(sputnikProperties.getHomeDirectory(), SOURCES_PATH);
    }

    @Override
    public Collection<DataProfile> getDataProfiles() {
        return readProfiles().values();
    }

    @Override
    public DataProfile getDataProfile(String name) {
        return notNull(readProfile(name));
    }

    @Override
    @SneakyThrows
    public void saveDataProfile(DataProfile dataProfile) {
        File file = new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR + "/" + dataProfile.getName());
        objectMapper.writeValue(file, dataProfile);
    }

    @Override
    public Collection<DataSource> getDataSources() {
        Collection<DataSource> dataSources = readSources();
        resolveProfiles(dataSources);
        return dataSources;
    }

    @Override
    @SneakyThrows
    public void saveDataSource(DataSource dataSource) {
        Set<DataSource> sources = readSources();
        sources.add(dataSource);
        objectMapper.writeValue(sourcesFile, sources);
    }

    @Override
    public File getDataFile(DataSource dataSource) {
        return new File(sputnikProperties.getHomeDirectory(), DATA_DIR + "/" + dataSource.getGroupName() + "/" + dataSource.getName());
    }

//    private

    private <T> T notNull(T src) {
        Assert.notNull(src);
        return src;
    }

    @SneakyThrows
    private Set<DataSource> readSources() {
        Set<DataSource> sources = new HashSet<>();
        if (sourcesFile.exists()) {
            DataSourceList list = objectMapper.readValue(sourcesFile, DataSourceList.class);
            sources.addAll(list);
        }
        return sources;
    }

    private void resolveProfiles(Collection<DataSource> dataSources) {
        Map<String, DataProfile> profiles = new HashMap<>();
        for(DataSource ds : dataSources) {
            DataProfile profile = profiles.get(ds.getDataProfileName());
            if (profile == null) {
                profile = readProfile(ds.getDataProfileName());
                profiles.put(ds.getDataProfileName(), profile);
            }
            ds.setDataProfile(profile);
        }
    }

    @SneakyThrows
    private Map<String, DataProfile> readProfiles() {
        File dir = new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR);
        Map<String, DataProfile> map = new HashMap<>();
        for(File file : dir.listFiles()) {
            DataProfile object = objectMapper.readValue(file, DataProfile.class);
            if (object != null) {
                map.put(object.getName(), object);
            }
        }
        return map;
    }

    @SneakyThrows
    private DataProfile readProfile(String name) {
        File file = new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR + "/" + name);
        return objectMapper.readValue(file, DataProfile.class);
    }

    private static class DataSourceList extends ArrayList<DataSource> {};
}
