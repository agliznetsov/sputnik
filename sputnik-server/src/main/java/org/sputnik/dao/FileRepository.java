package org.sputnik.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.Identifiable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class FileRepository<T extends Identifiable> implements InitializingBean, Repository<T> {

    @Autowired
    SputnikProperties sputnikProperties;
    @Autowired
    ObjectMapper objectMapper;

    protected final File basePath;
    private final Class<T> itemClass;
    private Map<String, T> items;

    @SneakyThrows
    public FileRepository(File path, Class<T> itemClass) {
        this.basePath = path;
        this.itemClass = itemClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!basePath.exists() && !basePath.mkdirs()) {
            throw new IOException("Failed to create " + basePath);
        }
    }

    @Override
    public T getOne(String id) {
        T item = getItems().get(id);
        Assert.notNull(item, "ID not found: " + id);
        return item;
    }

    @Override
    public void save(T item) {
        checkUniqueKey(item);
        if (item.getId() == null) {
            item.setId(createId());
        }
        write(item);
        getItems().put(item.getId(), item);
    }

    @Override
    @SneakyThrows
    public void delete(String id) {
        T item = getOne(id);
        File path = getPath(item);
        if (!path.delete()) {
            throw new IOException("Failed to delete " + path);
        }
        getItems().remove(id);
    }

    @Override
    public Collection<T> findAll() {
        return getItems().values();
    }

    protected File getPath(T item) {
        return new File(basePath, item.getKey());
    }

    protected void afterRead(T item) {
    }

    protected void beforeWrite(T item) {
    }

    private Map<String, T> readAll() {
        Map<String, T> map = new HashMap<>();
        readDirectory(basePath, map);
        return map;
    }

    @SneakyThrows
    private void readDirectory(File parent, Map<String, T> map) {
        for (File file : parent.listFiles()) {
            if (file.isFile()) {
                T object = objectMapper.readValue(file, itemClass);
                if (object.getId() == null) {
                    object.setId(createId());
                }
                if (object.getName() == null || object.getName().length() == 0) {
                    object.setName(file.getName());
                }
                afterRead(object);
                map.put(object.getId(), object);
            } else {
                readDirectory(new File(parent, file.getName()), map);
            }
        }
    }

    @SneakyThrows
    private void write(T item) {
        beforeWrite(item);
        File file = getPath(item);
        file.getParentFile().mkdirs();
        objectMapper.writeValue(file, item);
    }

    private String createId() {
        return UUID.randomUUID().toString();
    }

    private void checkUniqueKey(T item) {
        for (T it : getItems().values()) {
            if (it.getKey().equals(item.getKey()) && !it.getId().equals(item.getId())) {
                throw new IllegalStateException("Duplicate key: " + item.getKey());
            }
        }
    }

    private synchronized Map<String, T> getItems() {
        if (items == null) {
            items = readAll();
        }
        return items;
    }
}
