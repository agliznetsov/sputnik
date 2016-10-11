package org.sputnik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class ConfigController {

    @Autowired
    ConfigService configService;
    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ObjectMapper objectMapper;

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

    @RequestMapping("/properties")
    public Map getProperties() {
        Map map = objectMapper.convertValue(sputnikProperties, Map.class);
        map.remove("homeDirectory"); //for security reasons
        return map;
    }

}
