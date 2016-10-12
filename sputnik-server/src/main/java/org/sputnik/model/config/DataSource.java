package org.sputnik.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = {"groupName", "name"})
public class DataSource {
    @NotNull
    String groupName;
    @NotNull
    String name;
    String description;
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
