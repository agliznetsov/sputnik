package org.sputnik.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.sputnik.SputnikApplicationTests;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ConfigServiceTest extends SputnikApplicationTests {

    @Autowired
    ConfigService configService;

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(sputnikProperties.getHomeDirectory());
        configService.refresh();
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

        configService.refresh();
        assertEquals(1, configService.getDataProfiles().size());
        assertEquals(1, configService.getDataSources().size());
        assertEquals("test_profile", configService.getDataSource(dataSource.getName()).getDataProfile().getName());
    }
}
