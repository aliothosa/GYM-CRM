package com.elioth.epam.gymcrm.dto.request;


/*. Username (required)
II. First Name (required)
III. Last Name (required)
IV. Specialization (read only) (Training type reference)
V. Is Active (required)*/
public record UpdateTrainerRequest(
    String username,
    String firstName,
    String lastName,
    String trainingType,
    Boolean isActive
) {}
