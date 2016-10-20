package org.sputnik.controller.dto;

import lombok.Data;
import org.sputnik.model.CollectStatus;
import org.sputnik.model.config.DataFormat;

@Data
public class DataSourceDTO {
    String id;
    String groupName;
    String name;
    String description;
    String url;
    DataFormat dataFormat;
    String dataProfileName;
    boolean enabled = true;

    CollectStatus status;
}
