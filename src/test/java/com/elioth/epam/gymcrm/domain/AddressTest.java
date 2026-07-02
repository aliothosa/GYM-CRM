package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    @Test
    void shouldExposeAllFieldsViaAllArgsConstructor() {
        Address address = new Address("Main St", "Boston", "MA", "02101", 42);

        assertEquals("Main St", address.getStreet());
        assertEquals("Boston", address.getCity());
        assertEquals("MA", address.getState());
        assertEquals("02101", address.getZipCode());
        assertEquals(42, address.getNumber());
    }

    @Test
    void shouldAllowSettingFieldsViaSetters() {
        Address address = new Address();
        address.setStreet("Oak Ave");
        address.setCity("Cambridge");
        address.setState("MA");
        address.setZipCode("02139");
        address.setNumber(10);

        assertEquals("Oak Ave", address.getStreet());
        assertEquals("Cambridge", address.getCity());
        assertEquals("MA", address.getState());
        assertEquals("02139", address.getZipCode());
        assertEquals(10, address.getNumber());
    }
}
