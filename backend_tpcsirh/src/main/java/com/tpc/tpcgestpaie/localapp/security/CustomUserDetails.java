package com.tpc.tpcgestpaie.localapp.security;

import com.tpc.tpcgestpaie.localapp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    // Constructor
    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Crée les autorités à partir des rôles de l'utilisateur
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Récupère le mot de passe
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Récupère le nom d'utilisateur
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Si le compte est expiré, retourne false, sinon true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Si le compte est verrouillé, retourne false, sinon true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Si les credentials sont expirés, retourne false, sinon true
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().getName().equalsIgnoreCase("ACTIVE"); // Si l'utilisateur est actif
    }


}
