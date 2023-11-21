package com.uexcel.authorizationserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uexcel.authorizationserver.model.UserModel;
import com.uexcel.authorizationserver.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class Registration {

    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody UserModel userModel) {
        return ResponseEntity.ok(userService.saveUser(userModel));
    }

}
