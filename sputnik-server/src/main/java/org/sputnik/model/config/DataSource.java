package org.sputnik.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class DataSource {
    String id;
    String groupName;
    String name;
    String description;
    String url;
    DataFormat dataFormat;
    String dataProfileName;
    boolean enabled = true;

    @JsonIgnore
    DataProfile dataProfile;

    @JsonIgnore
    public String getKey() {
        return groupName + "/" + name;
    }
}
