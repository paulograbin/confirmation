package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.persistence.EntityRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T extends AbstracEntity> implements EntityRepository<T, Long> {

    private long lastId = 1;
    private final Map<Long, T> map = new HashMap<>();


    @Override
    public <S extends T> S save(S entity) {
        entity.setId(lastId++);
        map.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<T> findById(Long aLong) {
        if (map.containsKey(aLong)) {
            return Optional.of(map.get(aLong));
        }

        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        return null;
    }

    @Override
    public Iterable<T> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
