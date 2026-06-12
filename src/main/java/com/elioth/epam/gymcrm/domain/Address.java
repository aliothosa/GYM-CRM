package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private int number;
}
