package com.paulograbin.confirmation.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;


@NoRepositoryBean
public interface EntityRepository<T, ID> extends CrudRepository<T, ID> {

    @Override
    <S extends T> S save(S entity);

    @Override
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    Optional<T> findById(ID id);

    @Override
    boolean existsById(ID id);

    @Override
    Iterable<T> findAll();

    @Override
    Iterable<T> findAllById(Iterable<ID> ids);

    @Override
    long count();

    @Override
    void deleteById(ID id);

    @Override
    void delete(T entity);

    @Override
    void deleteAll(Iterable<? extends T> entities);

    @Override
    void deleteAll();

    @Override
    void deleteAllById(Iterable<? extends ID> iterable);
}
