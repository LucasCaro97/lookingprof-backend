package com.lookingprof.lookingProf.config;

import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public User requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("No autenticado");
        }
        return user;
    }

    public boolean isAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof User;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    public void assertOwnerOrAdmin(Integer userId) {
        User current = requireUser();
        if (!current.getIdUser().equals(userId) && !isAdmin(current)) {
            throw new AccessDeniedException("No autorizado");
        }
    }
}
