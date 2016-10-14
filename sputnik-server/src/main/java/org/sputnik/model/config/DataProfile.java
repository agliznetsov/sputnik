package org.sputnik.model.config;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataProfile {
    String name;
    List<Graph> graphs = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
