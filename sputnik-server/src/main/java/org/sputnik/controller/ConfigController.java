package org.sputnik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.config.SputnikProperties;
import org.sputnik.controller.dto.DataSourceDTO;
import org.sputnik.dao.Repository;
import org.sputnik.model.config.DataProfile;
import org.sputnik.model.config.DataSource;
import org.sputnik.service.CollectorService;
import org.sputnik.util.MapUtils;
import org.sputnik.util.NameUtils;
import org.sputnik.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ConfigController {

    @Autowired
    Repository<DataSource> dataSourceRepository;
    @Autowired
    Repository<DataProfile> dataProfileRepository;
    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    CollectorService collectorService;

    @RequestMapping("/dataProfiles")
    public Collection<DataProfile> getDataProfiles() {
        return dataProfileRepository.findAll();
    }

    @RequestMapping(value = "/dataProfiles", method = RequestMethod.POST)
    public void saveDataProfile(@RequestBody DataProfile dataProfile) {
        SecurityUtils.getUser(request);
        NameUtils.validateIdentifier(dataProfile.getName(), "name");
        dataProfileRepository.save(dataProfile);
    }

    @RequestMapping(value = "/dataProfiles/{id}", method = RequestMethod.DELETE)
    public void deleteDataProfile(@PathVariable("id") String id) {
        SecurityUtils.getUser(request);
        dataProfileRepository.delete(id);
    }


    @RequestMapping("/dataSources")
    public Collection<DataSourceDTO> getDataSources() {
        Collection<DataSource> sources = dataSourceRepository.findAll();
        return sources.stream().map(this::convertDataSource).collect(Collectors.toList());
    }

    private DataSourceDTO convertDataSource(DataSource source) {
        DataSourceDTO dto = objectMapper.convertValue(source, DataSourceDTO.class);
        dto.setStatus(collectorService.getStatus(source));
        return dto;
    }

    @RequestMapping(value = "/dataSources", method = RequestMethod.POST)
    public Map saveDataSource(@RequestBody DataSource dataSource) {
        SecurityUtils.getUser(request);
        validateDataSource(dataSource);
        dataSourceRepository.save(dataSource);
        return MapUtils.map("id", dataSource.getId());
    }

    @RequestMapping(value = "/dataSources/{id}", method = RequestMethod.DELETE)
    public void deleteDataSource(@PathVariable("id") String id) {
        SecurityUtils.getUser(request);
        dataSourceRepository.delete(id);
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
