package com.elioth.epam.gymcrm.dto.response;

import com.elioth.epam.gymcrm.domain.Address;

import java.time.LocalDate;

public record TraineeResponse(
        Long traineeId,
        Long userId,
        String firstName,
        String lastName,
        String username,
        Boolean active,
        LocalDate birthDate,
        Address address
) {}
