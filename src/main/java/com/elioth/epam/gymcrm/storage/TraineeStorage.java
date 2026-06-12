package com.elioth.epam.gymcrm.storage;

import com.elioth.epam.gymcrm.domain.Trainee;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TraineeStorage implements Storage<UUID, Trainee> {
    private final Map<UUID, Trainee> data = new HashMap<>();

    @Override
    public Map<UUID, Trainee> getData() {
        return data;
    }


}
