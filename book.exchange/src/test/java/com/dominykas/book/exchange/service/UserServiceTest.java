package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.userDTO.ChangePasswordRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserUpdateRequestDTO;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldSaveUserWithEncodedPassword() {
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("test@gmail.com");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");

        User savedUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO result = userService.createUser(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findByEmail("test@gmail.com");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findByEmail("missing@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("missing@gmail.com"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void findById_ShouldReturnUser() {
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setEmail("new@gmail.com");
        request.setFirstName("New");
        request.setLastName("Name");

        User user = User.builder()
                .id(1L)
                .email("old@gmail.com")
                .firstName("Old")
                .lastName("Name")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .email("new@gmail.com")
                .firstName("New")
                .lastName("Name")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(savedUser);

        UserResponseDTO result = userService.updateUser(1L, request);

        assertThat(result.getEmail()).isEqualTo("new@gmail.com");
        assertThat(result.getFirstName()).isEqualTo("New");

        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WhenEmailAlreadyExists_ShouldThrowException() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setEmail("taken@gmail.com");

        User currentUser = User.builder().id(1L).email("old@gmail.com").build();
        User otherUser = User.builder().id(2L).email("taken@gmail.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("taken@gmail.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void changePassword_ShouldEncodeAndSavePassword() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newPassword");

        User user = User.builder()
                .id(1L)
                .password("oldPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.changePassword(1L, request);

        assertThat(user.getPassword()).isEqualTo("encodedPassword");

        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        User user = User.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}