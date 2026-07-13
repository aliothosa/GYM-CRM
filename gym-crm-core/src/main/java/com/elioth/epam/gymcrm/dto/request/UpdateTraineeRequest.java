package com.elioth.epam.gymcrm.dto.request;

import com.elioth.epam.gymcrm.domain.Address;

import java.time.LocalDate;

public record UpdateTraineeRequest(
    String username,
    String firstName,
    String lastName,
    LocalDate birthDate,
    Address address,
    Boolean isActive
) {}
