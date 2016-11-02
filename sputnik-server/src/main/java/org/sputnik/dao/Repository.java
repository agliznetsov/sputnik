package org.sputnik.dao;

import org.sputnik.model.Identifiable;

import java.util.Collection;

public interface Repository<T extends Identifiable> {
    T getOne(String id);

    void save(T item);

    void delete(String id);

    Collection<T> findAll();
}
