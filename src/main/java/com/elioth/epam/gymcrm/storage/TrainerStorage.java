package com.elioth.epam.gymcrm.storage;


import com.elioth.epam.gymcrm.domain.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TrainerStorage implements Storage<UUID, Trainer> {
    private final Map<UUID, Trainer> data = new HashMap<>();

    @Override
    public Map<UUID, Trainer> getData() {
        return data;
    }
}
