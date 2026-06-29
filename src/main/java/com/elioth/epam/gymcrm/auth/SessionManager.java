package com.elioth.epam.gymcrm.auth;

import com.elioth.epam.gymcrm.exception.UserNotAuthenticatedException;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private AuthSession currentSession;

    public void login(AuthSession session) {
        this.currentSession = session;
    }

    public void logout() {
        this.currentSession = null;
    }

    public boolean isAuthenticated() {
        return currentSession != null;
    }

    public AuthSession getCurrentSession() {
        if (currentSession == null) {
            throw new UserNotAuthenticatedException("No user is logged in");
        }
        return currentSession;
    }
}
