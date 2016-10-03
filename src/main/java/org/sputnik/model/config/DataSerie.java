package org.sputnik.model.config;

import lombok.Data;

@Data
public class DataSerie {
    String path;
    String description;
    DataSerieType type;
}
