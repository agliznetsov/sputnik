package org.sputnik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Identifiable {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    @JsonIgnore
    String getKey();
}
