package org.sputnik.model.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph {
    String name;
    List<DataSerie> dataSeries = new ArrayList<>();
}
