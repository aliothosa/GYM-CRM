package com.elioth.epam.gymcrm.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class UserLogger {

    private static final Logger LOG = LoggerFactory.getLogger("USER_ACTIVITY");

    public void log(String username, String content,  Object... args) {
        try ( MDC.MDCCloseable ignored = MDC.putCloseable("username", username)) {
            LOG.info(content, args);
        }
    }
}
