package org.sputnik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.util.Collection;
import java.util.List;

@RestController
public class ConfigController {

    @Autowired
    ConfigService configService;

    @RequestMapping("/dataProfiles")
    public Collection<DataProfile> getDataProfiles() {
        return configService.getDataProfiles();
    }

    @RequestMapping("/dataProfiles/{name}")
    public DataProfile getDataProfile(@PathVariable("name") String name) {
        return configService.getDataProfile(name);
    }

    @RequestMapping(value = "/dataProfiles", method = RequestMethod.POST)
    public void saveDataProfile(@RequestBody DataProfile dataProfile) {
        configService.saveDataProfile(dataProfile);
    }

    @RequestMapping("/dataSources")
    public Collection<DataSource> getDataSources() {
        return configService.getDataSources();
    }

    @RequestMapping("/dataSources/{name}")
    public DataSource getDataSource(@PathVariable("name") String name) {
        return configService.getDataSource(name);
    }

    @RequestMapping(value = "/dataSources", method = RequestMethod.POST)
    public void saveDataProfile(@RequestBody DataSource dataSource) {
        configService.saveDataSource(dataSource);
    }

}
