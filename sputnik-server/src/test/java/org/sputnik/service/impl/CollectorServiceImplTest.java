package org.sputnik.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.sputnik.model.config.AggregateFunction;
import org.sputnik.model.config.DataSerie;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CollectorServiceImplTest {
    CollectorServiceImpl collectorService;

    @Before
    public void setUp() throws Exception {
        collectorService = new CollectorServiceImpl();
    }

    @Test
    public void test_getValue() throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("heap.used", "1");
        data.put("gc.sweep.time", 2);
        data.put("gc.sweep.counter", "3");
        data.put("gc.mark.time", 4);
        data.put("gc.mark.counter", 5);
        {
            DataSerie ds = new DataSerie();
            ds.setName("heap.used");
            assertEquals(1.0, collectorService.getValue(data, ds), 0.0);
        }
        {
            DataSerie ds = new DataSerie();
            ds.setPattern("gc(.*?)time");
            ds.setAggregateFunction(AggregateFunction.SUM);
            assertEquals(6.0, collectorService.getValue(data, ds), 0.0);
        }
        {
            DataSerie ds = new DataSerie();
            ds.setPattern("gc(.*?)counter");
            ds.setAggregateFunction(AggregateFunction.SUM);
            assertEquals(8.0, collectorService.getValue(data, ds), 0.0);
        }
    }
}
