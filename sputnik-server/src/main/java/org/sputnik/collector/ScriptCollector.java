package org.sputnik.collector;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.config.DataSource;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScriptCollector implements DataCollector {

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    TaskExecutor outputReader;

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

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(file.getAbsolutePath());
        Process process = pb.start();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outputReader.execute(() -> this.readOutput(process, out));
        if (!process.waitFor(sputnikProperties.getTimeout(), TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();
            throw new IllegalStateException("Timeout while executing " + file.getAbsolutePath());
        } else {
            String error = StreamUtils.copyToString(process.getErrorStream(), Charset.defaultCharset());
            if (!StringUtils.isEmpty(error)) {
                log.error(error);
            }
            return new String(out.toByteArray());
        }
    }

    @SneakyThrows
    private void readOutput(Process process, ByteArrayOutputStream out) {
        StreamUtils.copy(process.getInputStream(), out);
    }

}
