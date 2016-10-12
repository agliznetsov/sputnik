package org.sputnik.context;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class CpuMetrics implements PublicMetrics, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> result = new LinkedHashSet<>();
        addCpuMetrics(result);
        return result;
    }

    protected void addCpuMetrics(Collection<Metric<?>> result) {
        OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
        if (mxBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) mxBean;
            result.add(new Metric<>("cpu.process", osBean.getProcessCpuLoad()));
            result.add(new Metric<>("cpu.system", osBean.getSystemCpuLoad()));
        }
    }

}