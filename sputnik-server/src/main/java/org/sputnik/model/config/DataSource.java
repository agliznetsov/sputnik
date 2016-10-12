package org.sputnik.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DataSource {
    @NotNull
    String host;
    @NotNull
    String name;
    String url;
    DataFormat dataFormat;
    @NotNull
    String dataProfileName;

    @JsonIgnore
    DataProfile dataProfile;

    @Override
    public String toString() {
        return name;
    }
}
