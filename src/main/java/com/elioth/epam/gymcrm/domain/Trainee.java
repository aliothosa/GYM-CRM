package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Trainee extends User{
    private Date birthDate;
    private Address address;
}
