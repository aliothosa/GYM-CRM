package com.elioth.epam.gymcrm.dao.impl;

import com.elioth.epam.gymcrm.dao.BaseDao;
import com.elioth.epam.gymcrm.dao.TraineeDao;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.storage.TraineeStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class TraineeDaoImpl implements TraineeDao {
    private TraineeStorage storage;

    @Autowired
    public void setStorage(TraineeStorage storage){
        this.storage = storage;
    }

    @Override
    public Trainee create(Trainee entity) {
        if (!storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return storage.getData().get(entity.getId());
    }

    @Override
    public Trainee update(Trainee entity) {
        if (!storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return storage.getData().get(entity.getId());
    }

    @Override
    public void delete(UUID entityId) {
        storage.getData().remove(entityId);
    }

    @Override
    public List<Trainee> findAll() {
        return storage.getData().values().stream().toList();
    }

    @Override
    public Optional<Trainee> findById(UUID uuid) {
        return Optional.ofNullable(storage.getData().get(uuid));
    }
}
