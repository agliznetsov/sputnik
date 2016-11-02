package org.sputnik.model.config;

import lombok.Data;
import org.sputnik.model.Identifiable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataProfile implements Identifiable {
    String id;
    String name;
    List<Graph> graphs = new ArrayList<>();

    @Override
    public String getKey() {
        return name;
    }
}
