package org.sputnik.model;

import lombok.Data;

@Data
public class CollectStatus {
    long time;
    boolean ok;
    String errorMessage;
}
