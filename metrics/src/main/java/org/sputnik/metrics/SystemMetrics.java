package org.sputnik.metrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class SystemMetrics implements PublicMetrics {
    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> metrics = new LinkedHashSet<Metric<?>>();
        addManagementMetrics(metrics);
        addCpuMetrics(metrics);
        return metrics;
    }

    protected void addCpuMetrics(Collection<Metric<?>> result) {
        OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
        if (mxBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) mxBean;
            result.add(new Metric<>("cpu.process", osBean.getProcessCpuLoad()));
            result.add(new Metric<>("cpu.system", osBean.getSystemCpuLoad()));
        }
    }

    /**
     * Add metrics from ManagementFactory if possible. Note that ManagementFactory is not
     * available on Google App Engine.
     *
     * @param result the result
     */
    private void addManagementMetrics(Collection<Metric<?>> result) {
        try {
            // Add JVM up time in ms
            result.add(new Metric<Long>("uptime",
                    ManagementFactory.getRuntimeMXBean().getUptime()));
            result.add(new Metric<Double>("systemload.average",
                    ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
            addHeapMetrics(result);
            addNonHeapMetrics(result);
            addThreadMetrics(result);
            addClassLoadingMetrics(result);
            addGarbageCollectionMetrics(result);
        } catch (NoClassDefFoundError ex) {
            // Expected on Google App Engine
        }
    }

    /**
     * Add JVM heap metrics.
     *
     * @param result the result
     */
    protected void addHeapMetrics(Collection<Metric<?>> result) {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage();
        result.add(newMemoryMetric("heap.committed", memoryUsage.getCommitted()));
        result.add(newMemoryMetric("heap.init", memoryUsage.getInit()));
        result.add(newMemoryMetric("heap.used", memoryUsage.getUsed()));
        result.add(newMemoryMetric("heap", memoryUsage.getMax()));
    }

    /**
     * Add JVM non-heap metrics.
     *
     * @param result the result
     */
    private void addNonHeapMetrics(Collection<Metric<?>> result) {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean()
                .getNonHeapMemoryUsage();
        result.add(newMemoryMetric("nonheap.committed", memoryUsage.getCommitted()));
        result.add(newMemoryMetric("nonheap.init", memoryUsage.getInit()));
        result.add(newMemoryMetric("nonheap.used", memoryUsage.getUsed()));
        result.add(newMemoryMetric("nonheap", memoryUsage.getMax()));
    }

    private Metric<Long> newMemoryMetric(String name, long bytes) {
        return new Metric<Long>(name, bytes / 1024);
    }

    /**
     * Add thread metrics.
     *
     * @param result the result
     */
    protected void addThreadMetrics(Collection<Metric<?>> result) {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        result.add(new Metric<Long>("threads.peak",
                (long) threadMxBean.getPeakThreadCount()));
        result.add(new Metric<Long>("threads.daemon",
                (long) threadMxBean.getDaemonThreadCount()));
        result.add(new Metric<Long>("threads.totalStarted",
                threadMxBean.getTotalStartedThreadCount()));
        result.add(new Metric<Long>("threads", (long) threadMxBean.getThreadCount()));
    }

    /**
     * Add class loading metrics.
     *
     * @param result the result
     */
    protected void addClassLoadingMetrics(Collection<Metric<?>> result) {
        ClassLoadingMXBean classLoadingMxBean = ManagementFactory.getClassLoadingMXBean();
        result.add(new Metric<Long>("classes",
                (long) classLoadingMxBean.getLoadedClassCount()));
        result.add(new Metric<Long>("classes.loaded",
                classLoadingMxBean.getTotalLoadedClassCount()));
        result.add(new Metric<Long>("classes.unloaded",
                classLoadingMxBean.getUnloadedClassCount()));
    }

    /**
     * Add garbage collection metrics.
     *
     * @param result the result
     */
    protected void addGarbageCollectionMetrics(Collection<Metric<?>> result) {
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory
                .getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
            String name = beautifyGcName(garbageCollectorMXBean.getName());
            result.add(new Metric<Long>("gc." + name + ".count",
                    garbageCollectorMXBean.getCollectionCount()));
            result.add(new Metric<Long>("gc." + name + ".time",
                    garbageCollectorMXBean.getCollectionTime()));
        }
    }

    private String beautifyGcName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }

}
