package com.elioth.epam.gymcrm.dto.request;

public record ChangePasswordRequest(
       String oldPassword,
       String newPassword
) {}
