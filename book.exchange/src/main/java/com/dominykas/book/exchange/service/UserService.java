package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.userDTO.ChangePasswordRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserUpdateRequestDTO;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.mapper.UserMapper;
import com.dominykas.book.exchange.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <-- use existing bean

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        User user = UserMapper.fromDto(userRequestDTO);

        // encode password before saving
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserMapper.toDto(user);
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserMapper.toDto(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.findByEmail(dto.getEmail())
                .filter(foundUser -> !foundUser.getId().equals(id))
                .ifPresent(foundUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
                });

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // encode password before saving
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
    }
}