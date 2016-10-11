package org.sputnik.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.io.File;

@Data
@ConfigurationProperties(prefix = "sputnik")
public class SputnikProperties {
    File homeDirectory;

    /**
     * Data collection rate, sec
     */
    int dataRate = 60;

    int httpTimeout = 5000;

    int collectThreads = 5;

    int archiveRows = 1000;

    int archiveDays = 7;

    public SputnikProperties() {
        String home = System.getProperty("SPUTNIK_BASE");
        if (home == null || home.isEmpty()) {
            home = System.getProperty("user.dir");
        }
        homeDirectory = new File(home);
    }
}
