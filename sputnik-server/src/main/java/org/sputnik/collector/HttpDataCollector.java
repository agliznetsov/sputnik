package org.sputnik.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sputnik.model.config.DataSource;

@Service
public class HttpDataCollector implements DataCollector {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean canHandle(DataSource dataSource) {
        String url = dataSource.getUrl().toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @Override
    public String collectData(DataSource dataSource) {
        return restTemplate.getForObject(dataSource.getUrl(), String.class);
    }

}
