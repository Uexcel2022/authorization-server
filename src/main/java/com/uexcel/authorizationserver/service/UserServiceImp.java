package com.uexcel.authorizationserver.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.uexcel.authorizationserver.entity.Authority;
import com.uexcel.authorizationserver.entity.Role;
import com.uexcel.authorizationserver.entity.SecurityUsers;
import com.uexcel.authorizationserver.model.UserModel;
import com.uexcel.authorizationserver.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public String saveUser(UserModel userModel) {
        Authority authority = new Authority();
        SecurityUsers securityUsers = new SecurityUsers();
        securityUsers.setEmail(userModel.getEmail());
        securityUsers.setName(userModel.getName());
        securityUsers.setPassword(new BCryptPasswordEncoder().encode(userModel.getPassword()));
        securityUsers.setAccountNonExpired(true);
        securityUsers.setCredentialsNonExpired(true);
        securityUsers.setAccountNonLocked(true);
        securityUsers.setEnabled(true);

        Set<Authority> auth = new HashSet<>();
        authority.setRole(Role.USER.toString());
        auth.add(authority);
        securityUsers.setAuthorities(auth);

        userRepository.save(securityUsers);

        return "You have been registered successfully.";
    }

}
