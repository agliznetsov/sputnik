package org.sputnik.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

public class DaoTest {

    DataProfileRepository dataProfileRepository;
    DataSourceRepository dataSourceRepository;

    @Before
    public void setUp() throws Exception {
        File home = new File(System.getProperty("java.io.tmpdir"), "/sputnik-tests");
        FileUtils.deleteDirectory(home);

        ObjectMapper objectMapper = new ObjectMapper();

        dataProfileRepository = new DataProfileRepository(new File(home, "profiles"));
        dataProfileRepository.objectMapper = objectMapper;
        dataProfileRepository.afterPropertiesSet();

        dataSourceRepository = new DataSourceRepository(new File(home, "sources"));
        dataSourceRepository.objectMapper = objectMapper;
        dataSourceRepository.dataProfileRepository = dataProfileRepository;
        dataSourceRepository.afterPropertiesSet();
    }

    @Test
    public void read_write_config() throws Exception {
        DataProfile dataProfile = new DataProfile();
        dataProfile.setName("test_profile");
        dataProfileRepository.save(dataProfile);
        assertNotNull(dataProfile.getId());

        DataSource dataSource = new DataSource();
        dataSource.setName("test_source");
        dataSource.setDataProfileName(dataProfile.getName());
        dataSourceRepository.save(dataSource);
        assertNotNull(dataSource.getId());

        assertEquals(1, dataSourceRepository.findAll().size());
        Collection<DataSource> sources = dataSourceRepository.findAll();
        assertEquals(1, sources.size());
        assertEquals("test_profile", sources.iterator().next().getDataProfile().getName());
    }

    @Test(expected = IllegalStateException.class)
    public void duplicate_key() throws Exception {
        {
            DataProfile dataProfile = new DataProfile();
            dataProfile.setName("test_profile");
            dataProfileRepository.save(dataProfile);
        }
        {
            DataProfile dataProfile = new DataProfile();
            dataProfile.setName("test_profile");
            dataProfileRepository.save(dataProfile);
        }
    }

    @Test
    public void update() throws Exception {
        String id;
        {
            DataProfile dataProfile = new DataProfile();
            dataProfile.setName("test_profile");
            dataProfileRepository.save(dataProfile);
            id = dataProfile.getId();
        }
        {
            DataProfile dataProfile = new DataProfile();
            dataProfile.setId(id);
            dataProfile.setName("test_profile2");
            dataProfileRepository.save(dataProfile);
        }
        Collection<DataProfile> profiles = dataProfileRepository.findAll();
        assertEquals(1, profiles.size());
        assertEquals("test_profile2", profiles.iterator().next().getName());
    }
}
