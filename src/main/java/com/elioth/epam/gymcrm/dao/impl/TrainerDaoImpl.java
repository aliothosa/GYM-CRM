package com.elioth.epam.gymcrm.dao.impl;

import com.elioth.epam.gymcrm.dao.BaseDao;
import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.storage.TrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TrainerDaoImpl implements TrainerDao {
    private TrainerStorage storage;

    @Autowired
    public void setStorage(TrainerStorage storage) {
        this.storage = storage;
    }

    @Override
    public Trainer create(Trainer entity) {
        if (!storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return storage.getData().get(entity.getId());
    }

    @Override
    public Trainer update(Trainer entity) {
        if (storage.getData().containsKey(entity.getId())) {
            storage.getData().put(entity.getId(), entity);
            return entity;
        }
        return null;
    }

    @Override
    public void delete(UUID entityId) {
        storage.getData().remove(entityId);
    }

    @Override
    public List<Trainer> findAll() {
        return storage.getData().values().stream().toList();
    }

    @Override
    public Optional<Trainer> findById(UUID uuid) {
        return Optional.ofNullable(storage.getData().get(uuid));
    }
}
