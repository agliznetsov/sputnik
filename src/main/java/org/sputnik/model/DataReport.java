package org.sputnik.model;

import lombok.Data;
import org.sputnik.model.config.DataProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataReport {
    DataProfile dataProfile;
    long from;
    long to;
    long now;
    long lastUpdate;
    long[] timestamps;
    Map<String, double[]> values = new HashMap<>();
}
