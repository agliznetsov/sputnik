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
}
