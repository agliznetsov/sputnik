package org.sputnik.metrics;

import java.util.Collection;

public interface PublicMetrics {

    /**
     * Return an indication of current state through metrics.
     * @return the public metrics
     */
    Collection<Metric<?>> metrics();

}