package org.sputnik.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sputnik.model.config.DataFormat;
import org.sputnik.model.config.DataSource;

import java.util.HashMap;
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
            Map source = restTemplate.getForObject(dataSource.getUrl(), Map.class);
            Map<String, Number> target = new HashMap<>();
            flatMap(source, target, null);
            return target;
        } else {
            throw new RuntimeException("Data format is not supported: " + dataSource.getDataFormat());
        }
    }

    private void flatMap(Map<?, ?> source, Map<String, Number> target, String prefix) {
        for (Map.Entry e : source.entrySet()) {
            String key = prefix == null ? e.getKey().toString() : prefix + "." + e.getKey();
            Object value = e.getValue();
            if (value instanceof Map) {
                flatMap((Map<?, ?>) value, target, key);
            } else if (value instanceof Number) {
                target.put(key, (Number) value);
            }
        }
    }

}
