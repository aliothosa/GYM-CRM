package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Optional/low-priority domain test; enable when practicing entity field assignment.")
class UserTest {

    @Test
    void shouldAllowSettingIdentityAndCredentials() {
        // TODO: create User; set firstName, lastName, username, password, active
        // TODO: assert getters return assigned values
    }

    @Test
    void shouldHaveNullUserIdBeforePersistence() {
        // TODO: create new User()
        // TODO: assert getUserId() is null (not yet persisted)
    }
}
