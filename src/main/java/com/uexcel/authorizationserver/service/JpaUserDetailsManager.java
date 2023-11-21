package com.uexcel.authorizationserver.service;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.uexcel.authorizationserver.entity.SecurityUsers;
import com.uexcel.authorizationserver.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SecurityUsers uSecurityUsers = userRepository.findByEmail(email);

        if (uSecurityUsers == null) {
            throw new UsernameNotFoundException("Bad credentials");
        }
        Collection<GrantedAuthority> getAuthorities = new HashSet<>();
        uSecurityUsers.getAuthorities()
                .forEach(auth -> getAuthorities.add(new SimpleGrantedAuthority(auth.getRole())));

        return new User(uSecurityUsers.getEmail(), uSecurityUsers.getPassword(),
                uSecurityUsers.isAccountNonLocked(), uSecurityUsers.isAccountNonExpired(),
                uSecurityUsers.isCredentialsNonExpired(),
                uSecurityUsers.isEnabled(), getAuthorities);

    }

    @Override
    public void createUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String email) {
        SecurityUsers users = userRepository.findByEmail(email);
        if (email.equals(users.getEmail())) {
            return true;
        }
        return false;
    }

}
