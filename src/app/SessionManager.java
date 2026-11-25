package app;

import model.User;
import model.enums.UserRole;

/**
 * Manages the runtime session of the currently authenticated user in CANDIDEX.
 * Demonstrates State Encapsulation and Access Control.
 */
public class SessionManager {

    private User currentUser;
    private boolean authenticated;

    public SessionManager() {
        this.currentUser = null;
        this.authenticated = false;
    }

    public void createSession(User user) {
        if (user != null) {
            this.currentUser = user;
            this.authenticated = true;
        }
    }

    public void destroySession() {
        this.currentUser = null;
        this.authenticated = false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean hasRole(UserRole role) {
        return authenticated && currentUser != null && currentUser.getRole() == role;
    }
}
