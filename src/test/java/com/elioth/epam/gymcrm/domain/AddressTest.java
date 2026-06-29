package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Optional/low-priority domain test; enable when practicing embeddable field assignment.")
class AddressTest {

    @Test
    void shouldExposeAllFieldsViaAllArgsConstructor() {
        // TODO: create Address("Main St", "Boston", "MA", "02101", 42)
        // TODO: assert street, city, state, zipCode, and number match constructor args
    }

    @Test
    void shouldAllowSettingFieldsViaSetters() {
        // TODO: create empty Address; set street, city, state, zipCode, number
        // TODO: assert getters return assigned values
    }
}
