package org.sputnik.metrics;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class JettyMetrics implements PublicMetrics {
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName;
    String[] attributes = new String[]{"requests", "responses1xx", "responses2xx", "responses3xx", "responses4xx", "responses5xx"};
    String[] names = new String[]{"requests", "counter.status.100", "counter.status.200", "counter.status.300", "counter.status.400", "counter.status.500"};

    public JettyMetrics() {
        Set<ObjectInstance> instances = null;
        try {
            objectName = new ObjectName("org.eclipse.jetty.server.handler:id=0,type=statisticshandler");
            server.getObjectInstance(objectName);
        } catch (Exception e) {
            e.printStackTrace();
            objectName = null;
        }
    }

    @Override
    public Collection<Metric<?>> metrics() {
        if (objectName != null) {
            try {
                Collection<Metric<?>> metrics = new ArrayList<>();
                AttributeList values = server.getAttributes(objectName, attributes);
                for (Attribute a : values.asList()) {
                    for (int i = 0; i < attributes.length; i++) {
                        if (attributes[i].equals(a.getName())) {
                            metrics.add(new Metric<Number>(names[i], (Number) a.getValue()));
                        }
                    }
                }
                return metrics;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}
