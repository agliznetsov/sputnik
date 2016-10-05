package org.sputnik.model.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph {
    String name;
    String description;
    List<DataSerie> dataSeries = new ArrayList<>();
}
