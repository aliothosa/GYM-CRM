package com.elioth.epam.gymcrm.storage;

import com.elioth.epam.gymcrm.domain.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class TrainingStorage implements Storage<Long, Training> {
    private final Map<Long, Training> data = new HashMap<>();

    @Override
    public Map<Long, Training> getData() {
        return data;
    }
}
