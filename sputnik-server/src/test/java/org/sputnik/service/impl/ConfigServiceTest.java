package org.sputnik.service.impl;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.sputnik.config.SputnikApplicationTests;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ConfigServiceTest extends SputnikApplicationTests {

    @Autowired
    ConfigServiceImpl configService;

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(sputnikProperties.getHomeDirectory());
        configService.afterPropertiesSet();
        assertEquals(0, configService.getDataProfiles().size());
        assertEquals(0, configService.getDataSources().size());
    }

    @Test
    public void read_write_config() throws Exception {
        DataProfile dataProfile = new DataProfile();
        dataProfile.setName("test_profile");
        configService.saveDataProfile(dataProfile);

        DataSource dataSource = new DataSource();
        dataSource.setName("test_source");
        dataSource.setDataProfileName(dataProfile.getName());
        configService.saveDataSource(dataSource);

        assertEquals(1, configService.getDataProfiles().size());
        Collection<DataSource> sources = configService.getDataSources();
        assertEquals(1, sources.size());
        assertEquals("test_profile", sources.iterator().next().getDataProfile().getName());
    }
}
