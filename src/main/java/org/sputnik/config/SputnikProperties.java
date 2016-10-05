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

    int scheduleThreads = 1;

    int executeThreads = 5;

    int archiveRows = 1000;

    int days = 7;
}
