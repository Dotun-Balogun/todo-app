package edu.plapoly.todo.security;

import edu.plapoly.todo.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    public User resolve(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails details)) {
            throw new IllegalStateException("No authenticated user in this request.");
        }
        return details.getUser();
    }
}
