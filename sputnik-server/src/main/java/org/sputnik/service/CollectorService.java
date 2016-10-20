package org.sputnik.service;

import org.sputnik.model.CollectStatus;
import org.sputnik.model.config.DataSource;

public interface CollectorService {
    void collect();

    CollectStatus getStatus(DataSource source);
}
