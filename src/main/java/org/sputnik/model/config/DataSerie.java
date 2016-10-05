package org.sputnik.model.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = "name")
public class DataSerie {
    @NotNull
    String name;
    String pattern;
    String description;
    DataSerieType serieType = DataSerieType.GAUGE;
    AggregateFunction aggregateFunction;
    DrawType drawType = DrawType.LINE;
    String color;
    Double multiplier;
    Double max;
    Double min;
}
