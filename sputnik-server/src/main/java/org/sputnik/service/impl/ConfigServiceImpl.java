package org.sputnik.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private Map<String, DataProfile> profiles;

    @Override
    public void afterPropertiesSet() throws Exception {
        new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR).mkdirs();
        new File(sputnikProperties.getHomeDirectory(), DATA_DIR).mkdirs();
        sourcesFile = new File(sputnikProperties.getHomeDirectory(), SOURCES_PATH);
        profiles = readProfiles();
    }

    @Override
    public Collection<DataProfile> getDataProfiles() {
        return profiles.values();
    }

    @Override
    public DataProfile getDataProfile(String name) {
        return notNull(profiles.get(name));
    }

    @Override
    @SneakyThrows
    public void saveDataProfile(DataProfile dataProfile) {
        File file = new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR + "/" + dataProfile.getName());
        objectMapper.writeValue(file, dataProfile);
        profiles.put(dataProfile.getName(), dataProfile);
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
        try {
            getDataProfile(dataSource.getDataProfileName());
        } catch (Exception e) {
            log.error("Error", e);
            throw new IllegalArgumentException("Invalid data profile name");
        }
        DataSourceList sources = readSources();

        if (dataSource.getId() == null) {
            dataSource.setId(UUID.randomUUID().toString());
            sources.add(dataSource);
        } else {
            sources.update(dataSource);
        }

        sources.checkNames();
        objectMapper.writeValue(sourcesFile, sources);
    }

    @Override
    @SneakyThrows
    public void deleteDataSource(String id) {
        DataSourceList sources = readSources();
        boolean found = false;
        for (int i = 0; i < sources.size(); i++) {
            if (id.equals(sources.get(i).getId())) {
                sources.remove(i);
                found = true;
                break;
            }
        }
        if (found) {
            objectMapper.writeValue(sourcesFile, sources);
        } else {
            throw new IllegalArgumentException("Data source not found id: " + id);
        }
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
    private DataSourceList readSources() {
        if (sourcesFile.exists()) {
            DataSourceList list = objectMapper.readValue(sourcesFile, DataSourceList.class);
            list.checkNames();
            return list;
        } else {
            return new DataSourceList();
        }
    }

    private void resolveProfiles(Collection<DataSource> dataSources) {
        Map<String, DataProfile> profiles = new HashMap<>();
        for (DataSource ds : dataSources) {
            DataProfile profile = profiles.get(ds.getDataProfileName());
            if (profile == null) {
                profile = getDataProfile(ds.getDataProfileName());
                profiles.put(ds.getDataProfileName(), profile);
            }
            ds.setDataProfile(profile);
        }
    }

    @SneakyThrows
    private Map<String, DataProfile> readProfiles() {
        File dir = new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR);
        Map<String, DataProfile> map = new HashMap<>();
        for (File file : dir.listFiles()) {
            DataProfile object = objectMapper.readValue(file, DataProfile.class);
            if (object != null) {
                map.put(object.getName(), object);
            }
        }
        return map;
    }

    private static class DataSourceList extends ArrayList<DataSource> {
        public void checkNames() {
            Set<String> names = new HashSet<>();
            for (DataSource ds : this) {
                if (!names.add(ds.getKey())) {
                    throw new IllegalStateException("Duplicate data source name: " + ds.getKey());
                }
            }
        }

        public void update(DataSource dataSource) {
            for (int i = 0; i < size(); i++) {
                if (get(i).getId() != null && get(i).getId().equals(dataSource.getId())) {
                    this.set(i, dataSource);
                    return;
                }
            }
            throw new IllegalArgumentException("Datasource with id " + dataSource.getId() + " not found");
        }
    }
}
