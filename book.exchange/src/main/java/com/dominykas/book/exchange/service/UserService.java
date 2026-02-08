package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.mapper.UserMapper;
import com.dominykas.book.exchange.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        User user = UserMapper.fromDto(userRequestDTO);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }
}
