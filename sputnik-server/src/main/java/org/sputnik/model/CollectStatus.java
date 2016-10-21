package org.sputnik.model;

import lombok.Data;

@Data
public class CollectStatus {
    long updated;
    int ping;
    boolean ok;
    String errorMessage;
}
