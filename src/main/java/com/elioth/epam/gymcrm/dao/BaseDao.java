package com.elioth.epam.gymcrm.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseDao<T, ID> {
    T create(T entity);

    T update(T entity);

    void delete(ID entityID);

    List<T> findAll();

    Optional<T> findById(ID id);
}
