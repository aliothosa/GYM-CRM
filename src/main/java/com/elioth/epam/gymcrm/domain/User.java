package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
}
