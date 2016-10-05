package org.sputnik.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sputnik.model.config.DataFormat;
import org.sputnik.model.config.DataSource;

import java.util.Map;

@Service
public class HttpDataCollector implements DataCollector {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean canHandle(DataSource dataSource) {
        return dataSource.getUrl().toLowerCase().startsWith("http://");
    }

    @Override
    public Map<String, Number> collectData(DataSource dataSource) {
        if (dataSource.getDataFormat() == null || dataSource.getDataFormat() == DataFormat.JSON) {
            return restTemplate.getForObject(dataSource.getUrl(), Map.class);
        } else {
            throw new RuntimeException("Data format is not supported: " + dataSource.getDataFormat());
        }
    }

}
