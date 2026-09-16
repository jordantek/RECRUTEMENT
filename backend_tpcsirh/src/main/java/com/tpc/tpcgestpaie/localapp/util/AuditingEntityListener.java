package com.tpc.tpcgestpaie.localapp.util;

import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class AuditingEntityListener {
    private final UserRepository userRepository;

    public AuditingEntityListener( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PrePersist
    public void setCreatedBy(Auditable entity) {
        Long userId = getCurrentUser().getId();
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
    }

    @PreUpdate
    public void setUpdatedBy(Auditable entity) {
        Long userId = getCurrentUser().getId();
        entity.setUpdatedBy(userId);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails currentUser_ =  (UserDetails) principal;
        return userRepository.findByUsername(currentUser_.getUsername());
    }

}
