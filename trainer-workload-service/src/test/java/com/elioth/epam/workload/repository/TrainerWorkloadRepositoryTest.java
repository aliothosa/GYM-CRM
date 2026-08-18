package com.elioth.epam.workload.repository;

import com.elioth.epam.workload.persistence.TrainerWorkloadDocument;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadRepositoryTest {

    @Test
    void providesBusinessLookupByUsernameAndRequiredMongoIndexes() throws NoSuchFieldException, NoSuchMethodException {
        Method lookup = TrainerWorkloadRepository.class.getMethod("findByTrainerUsername", String.class);
        assertEquals(Optional.class, lookup.getReturnType());

        Field username = TrainerWorkloadDocument.class.getDeclaredField("trainerUsername");
        assertTrue(username.getAnnotation(Indexed.class).unique());
        CompoundIndex nameIndex = TrainerWorkloadDocument.class.getAnnotation(CompoundIndex.class);
        assertEquals("{'trainerFirstName': 1, 'trainerLastName': 1}", nameIndex.def());
    }
}
