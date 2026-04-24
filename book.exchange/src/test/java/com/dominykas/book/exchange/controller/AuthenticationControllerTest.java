package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.userDTO.AuthenticationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.LoginRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.service.AuthenticationService;
import com.dominykas.book.exchange.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController controller;

    @Test
    void createUser_ShouldReturnUserResponse() {
        UserRequestDTO request = new UserRequestDTO();
        AuthenticationResponseDTO response = new AuthenticationResponseDTO();

        when(authenticationService.register(request)).thenReturn(response);

        ResponseEntity<UserResponseDTO> result = controller.createUser(request);

        assertThat(result.getBody()).isSameAs(response);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        verify(authenticationService).register(request);
    }

    @Test
    void login_ShouldReturnAuthenticationResponse() {
        LoginRequestDTO request = new LoginRequestDTO();
        AuthenticationResponseDTO response = new AuthenticationResponseDTO();

        when(authenticationService.login(request)).thenReturn(response);

        ResponseEntity<AuthenticationResponseDTO> result = controller.login(request);

        assertThat(result.getBody()).isSameAs(response);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        verify(authenticationService).login(request);
    }

    @Test
    void checkEmail_ShouldReturnUserResponse() {
        String email = "test@gmail.com";
        UserResponseDTO response = new UserResponseDTO();

        when(userService.findByEmail(email)).thenReturn(response);

        ResponseEntity<UserResponseDTO> result = controller.checkEmail(email);

        assertThat(result.getBody()).isSameAs(response);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        verify(userService).findByEmail(email);
    }
}