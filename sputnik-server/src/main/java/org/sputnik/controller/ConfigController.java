package org.sputnik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.ConfigService;
import org.sputnik.util.MapUtils;
import org.sputnik.util.NameUtils;

import javax.websocket.server.PathParam;
import java.util.Collection;
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

    @RequestMapping(value = "/dataProfiles", method = RequestMethod.PUT)
    public void saveDataProfile(@RequestBody DataProfile dataProfile) {
        NameUtils.validateIdentifier(dataProfile.getName(), "name");
        configService.saveDataProfile(dataProfile);
    }

    @RequestMapping("/dataSources")
    public Collection<DataSource> getDataSources() {
        return configService.getDataSources();
    }

    @RequestMapping(value = "/dataSources", method = RequestMethod.POST)
    public Map createDataSource(@RequestBody DataSource dataSource) {
        if (dataSource.getId() != null) {
            throw new IllegalArgumentException("id is not null");
        }
        validateDataSource(dataSource);
        configService.saveDataSource(dataSource);
        return MapUtils.map("id", dataSource.getId());
    }

    @RequestMapping(value = "/dataSources/{id}", method = RequestMethod.PUT)
    public void updateDataSource(@RequestBody DataSource dataSource, @PathVariable("id") String id) {
        validateDataSource(dataSource);
        dataSource.setId(id);
        configService.saveDataSource(dataSource);
    }

    @RequestMapping(value = "/dataSources/{id}", method = RequestMethod.DELETE)
    public void deleteDataSource(@PathVariable("id") String id) {
        configService.deleteDataSource(id);
    }

    private void validateDataSource(@RequestBody DataSource dataSource) {
        NameUtils.validateIdentifier(dataSource.getName(), "name");
        NameUtils.validateIdentifier(dataSource.getGroupName(), "groupName");
    }

    @RequestMapping("/properties")
    public Map getProperties() {
        Map map = objectMapper.convertValue(sputnikProperties, Map.class);
        map.remove("homeDirectory"); //for security reasons
        return map;
    }

}
