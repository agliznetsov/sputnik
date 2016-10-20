package org.sputnik.collector;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSource;

import java.io.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class ScriptCollector implements DataCollector {

    @Autowired
    SputnikProperties sputnikProperties;

    @Override
    public boolean canHandle(DataSource dataSource) {
        String url = dataSource.getUrl().toLowerCase();
        return url.startsWith("file://");
    }

    @Override
    @SneakyThrows
    public String collectData(DataSource dataSource) {
        File file = new File(new URI(dataSource.getUrl()));
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        File out = File.createTempFile("out", "");
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(file.getAbsolutePath());
        pb.redirectOutput(out);
        Process process = pb.start();

        if (!process.waitFor(sputnikProperties.getHttpTimeout(), TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();
            out.delete();
            throw new IllegalStateException("Timeout while executing " + file.getAbsolutePath());
        } else {
            String body = FileUtils.readFileToString(out);
            out.delete();
            return body;
        }
    }

}
