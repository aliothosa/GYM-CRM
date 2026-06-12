package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Trainee extends User{
    private Date birthDate;
    private Address address;
}
