package org.sputnik.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.sputnik.config.SputnikConfig;
import org.sputnik.model.config.DataSerie;
import org.sputnik.model.config.DataSerieType;
import org.sputnik.model.config.Graph;

import static org.junit.Assert.*;

public class ConfigTest {
    ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new SputnikConfig().objectMapper();
    }

    @Test
    public void test_json() throws Exception {
        Graph graph = new Graph();
        DataSerie dataSerie = new DataSerie();
        dataSerie.setSerieType(DataSerieType.DERIVE);
        dataSerie.setName("a.b");
        graph.getDataSeries().add(dataSerie);
        assertEquals("{\"dataSeries\":[{\"name\":\"a.b\",\"serieType\":\"DERIVE\",\"fill\":false}]}", objectMapper.writeValueAsString(graph));
    }

}
