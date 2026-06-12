package com.elioth.epam.gymcrm.dao.impl;

import com.elioth.epam.gymcrm.dao.BaseDao;
import com.elioth.epam.gymcrm.dao.TrainingDao;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.rmi.server.UID;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TrainingDaoImpl implements TrainingDao {
    public TrainingStorage storage;

    @Autowired
    public void setStorage(TrainingStorage storage) {
        this.storage = storage;
    }

    @Override
    public Training create(Training entity) {
        if (!storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return storage.getData().get(entity.getId());
    }

    @Override
    public Training update(Training entity) {
        if  (storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return null;
    }

    @Override
    public void delete(Long entityID) {
        storage.getData().remove(entityID);
    }

    @Override
    public List<Training> findAll() {
        return storage.getData().values().stream().toList();
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.getData().get(id));
    }
}
