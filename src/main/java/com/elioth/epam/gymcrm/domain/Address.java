package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private int number;
}
