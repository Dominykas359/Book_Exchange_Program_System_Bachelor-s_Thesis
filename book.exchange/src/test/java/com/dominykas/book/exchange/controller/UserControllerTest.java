package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.userDTO.ChangePasswordRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserUpdateRequestDTO;
import com.dominykas.book.exchange.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void createUser_ShouldReturnResponse() {
        UserRequestDTO request = new UserRequestDTO();
        UserResponseDTO response = new UserResponseDTO();

        when(userService.createUser(request)).thenReturn(response);

        UserResponseDTO result = controller.createUser(request);

        assertThat(result).isSameAs(response);
        verify(userService).createUser(request);
    }

    @Test
    void getUserById_ShouldReturnResponse() {
        UserResponseDTO response = new UserResponseDTO();

        when(userService.findById(1L)).thenReturn(response);

        UserResponseDTO result = controller.getUserById(1L);

        assertThat(result).isSameAs(response);
        verify(userService).findById(1L);
    }

    @Test
    void getUserByEmail_ShouldReturnResponse() {
        UserResponseDTO response = new UserResponseDTO();

        when(userService.findByEmail("test@gmail.com")).thenReturn(response);

        UserResponseDTO result = controller.getUserByEmail("test@gmail.com");

        assertThat(result).isSameAs(response);
        verify(userService).findByEmail("test@gmail.com");
    }

    @Test
    void updateUser_ShouldReturnResponse() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        UserResponseDTO response = new UserResponseDTO();

        when(userService.updateUser(1L, request)).thenReturn(response);

        UserResponseDTO result = controller.updateUser(1L, request);

        assertThat(result).isSameAs(response);
        verify(userService).updateUser(1L, request);
    }

    @Test
    void changePassword_ShouldCallService() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();

        controller.changePassword(1L, request);

        verify(userService).changePassword(1L, request);
    }

    @Test
    void deleteUser_ShouldCallService() {
        controller.deleteUser(1L);

        verify(userService).deleteUser(1L);
    }
}