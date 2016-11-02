package org.sputnik.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.sputnik.model.Identifiable;

import java.net.URI;

@Data
public class DataSource implements Identifiable {
    String id;
    String groupName;
    String name;
    String description;
    String url;
    DataFormat dataFormat = DataFormat.JSON;
    String dataProfileName;
    boolean enabled = true;

    @JsonIgnore
    DataProfile dataProfile;

    @Override
    public String getKey() {
        return groupName + "/" + name;
    }
}
