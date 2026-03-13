package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.userDTO.AuthenticationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.LoginRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.service.AuthenticationService;
import com.dominykas.book.exchange.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody UserRequestDTO registrationRequestDTO
    ){
        return ResponseEntity.ok(authenticationService.register(registrationRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ){
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<UserResponseDTO> checkEmail(@PathVariable("email") String email){
        return ResponseEntity.ok(userService.findByEmail(email));
    }
}