//package org.sputnik.service.impl;
//
//import org.apache.commons.io.FileUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.rrd4j.core.FetchData;
//import org.rrd4j.core.FetchRequest;
//import org.rrd4j.core.RrdDb;
//import org.rrd4j.core.Util;
//import org.rrd4j.data.Variable;
//import org.rrd4j.graph.RrdGraph;
//import org.rrd4j.graph.RrdGraphDef;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.sputnik.SputnikApplicationTests;
//import org.sputnik.model.config.AggregateFunction;
//import org.sputnik.model.config.DataProfile;
//import org.sputnik.model.config.DataSerie;
//import org.sputnik.model.config.DataSerieType;
//import org.sputnik.model.config.DataSource;
//import org.sputnik.model.config.Graph;
//import org.sputnik.service.ConfigService;
//import org.sputnik.service.DBService;
//
//import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Random;
//
//import static org.rrd4j.ConsolFun.*;
//
//public class CollectorServiceTester extends SputnikApplicationTests {
//    static final Random RANDOM = new Random(0);
//    static final long START = Util.getTimestamp(2010, 1, 1);
//
//    @Autowired
//    ConfigService configService;
//    @Autowired
//    DBService dbService;
//    @Autowired
//    CollectorServiceImpl collectorService;
//
//    @Before
//    public void setUp() throws Exception {
//        FileUtils.deleteDirectory(sputnikProperties.getHomeDirectory());
//        configService.refresh();
//    }
//
//    @Test
//    public void test_heap() throws Exception {
//        DataProfile java = createJavaProfile();
//        DataSource ds = new DataSource();
//        ds.setDataProfile(java);
//        ds.setHost("host");
//        ds.setName("test");
//        File dataFile = configService.getDataFile(ds);
//        configService.saveDataProfile(java);
//        dbService.createDB(dataFile, ds);
//
//        RrdDb db = new RrdDb(dataFile.getPath());
//        Map<String, Number> data = new HashMap<>();
//        int used = 1000; //1mb
//        int n = 0;
//        long t = START;
//        while (t <= START + DBServiceImpl.YEAR) {
//            t += sputnikProperties.getDataRate();
//            n += sputnikProperties.getDataRate();
//            used += (RANDOM.nextDouble() * 10);
//            if (n > DBServiceImpl.DAY / 2) {
//                n = 0;
//                used = 1000;
//            }
//            data.put("heap.used", used);
//            collectorService.updateDB(db, ds, data, t);
//        }
//        db.close();
//        createGraph(dataFile.getPath(), "heap.used", "day", DBServiceImpl.DAY);
//        createGraph(dataFile.getPath(), "heap.used", "week", DBServiceImpl.WEEK);
//        createGraph(dataFile.getPath(), "heap.used", "month", DBServiceImpl.MONTH);
//        createGraph(dataFile.getPath(), "heap.used", "year", DBServiceImpl.YEAR);
//    }
//
//
//    private DataProfile createJavaProfile() {
//        DataProfile profile = new DataProfile();
//        profile.setName("java");
//
//        {
//            Graph graph = new Graph();
//            graph.setName("Heap");
//            DataSerie serie = new DataSerie();
//            serie.setName("heap.used");
//            serie.setMultiplier(1024.0);
//            graph.getDataSeries().add(serie);
//            profile.getGraphs().add(graph);
//        }
//
//        {
//            Graph graph = new Graph();
//            graph.setName("Request");
//            DataSerie serie = new DataSerie();
//            serie.setName("counter.status");
//            serie.setSerieType(DataSerieType.DERIVE);
//            serie.setAggregateFunction(AggregateFunction.SUM);
//            graph.getDataSeries().add(serie);
//            profile.getGraphs().add(graph);
//        }
//
//        return profile;
//    }
//
//    private void createGraph(String db, String name, String suffix, long duration) throws IOException {
//        RrdGraphDef gDef = new RrdGraphDef();
//        gDef.setLocale(Locale.US);
//        gDef.setWidth(1024);
//        gDef.setHeight(600);
//
//        gDef.setFilename(sputnikProperties.getHomeDirectory() + "/" + suffix + ".png");
//        gDef.setStartTime(START + DBServiceImpl.YEAR - duration);
//        gDef.setEndTime(START + DBServiceImpl.YEAR);
//        gDef.setTitle("Value by " + suffix);
////        gDef.setVerticalLabel("memory, bytes");
//
//        gDef.datasource("value", db, name, AVERAGE);
//        gDef.datasource("value.min", "value", new Variable.MIN());
//        gDef.datasource("value.max", "value", new Variable.MAX());
//
//        gDef.line("value", Color.GREEN);
////        gDef.line("value.min", Color.BLUE, "value.min");
////        gDef.line("value.max", Color.BLUE, "value.max");
//
//        gDef.setImageInfo("<img src='%s' width='%d' height = '%d'>");
//        gDef.setImageFormat("png");
//        RrdGraph graph = new RrdGraph(gDef);
//    }
//
//}
