//package org.sputnik.service.impl;
//
//import org.junit.Test;
//import org.rrd4j.DsType;
//import org.rrd4j.core.FetchData;
//import org.rrd4j.core.FetchRequest;
//import org.rrd4j.core.RrdDb;
//import org.rrd4j.core.RrdDef;
//import org.rrd4j.core.Sample;
//
//import static org.rrd4j.ConsolFun.*;
//
//public class RrdbTester {
//
//    public static final int START = 0;
//    public static final int STEP = 60;
//
//    @Test
//    public void test_fetch() throws Exception {
//        String path = "c:/tmp/rrd";
//
//        {
//            RrdDef rrdDef = new RrdDef(path, 0, STEP);
//            rrdDef.addDatasource("value", DsType.GAUGE, 600, Double.NaN, Double.NaN);
//            rrdDef.addArchive(AVERAGE, 0.5, 1, 10); // 10 samples
//
//            RrdDb db = new RrdDb(rrdDef);
//            int time = START;
//            int value = 0;
//            for (int i = 0; i < 60 * 10; i++) {
//                time += STEP;
//                value += 1;
//                Sample sample = db.createSample(time);
//                sample.setValue("value", 1);
//                sample.update();
//            }
//            System.out.println(db.dump());
//            db.close();
//        }
//
//
////        int end = START + STEP * 60 * 10;
////        FetchRequest request = db.createFetchRequest(AVERAGE, end - STEP * 10, end);
////        FetchData fetchData = request.fetchData();
////        System.out.println("== Data fetched, " + fetchData.getRowCount() + " points obtained");
////        System.out.println(fetchData.toString());
//    }
//
//    @Test
//    public void test_concurrent_access() throws Exception {
//        String path = "c:/tmp/rrd";
//
//        {
//            RrdDef rrdDef = new RrdDef(path, 0, STEP);
//            rrdDef.addDatasource("value", DsType.GAUGE, 600, Double.NaN, Double.NaN);
//            rrdDef.addArchive(AVERAGE, 0.5, 1, 10); // 10 samples
//            RrdDb db = new RrdDb(rrdDef);
//            db.close();
//        }
//
//        {
//            RrdDb db1 = new RrdDb(path);
//            Sample sample = db1.createSample(STEP);
//            sample.setValue("value", 1);
//            sample.update();
//
//
////            RrdDb db2 = new RrdDb(path);
////            Sample sample2 = db2.createSample(STEP);
////            sample2.setValue("value", 2);
////            sample2.update();
//
//            db1.close();
////            db2.close();
//        }
//
//        RrdDb db = new RrdDb(path);
//        System.out.println(db.dump());
//        db.close();
//    }
//
//}
