package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.entity.User;

public class UserMapper {

    public static User fromDto(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());

        return user;
    }

    public static UserResponseDTO toDto(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());

        return userResponseDTO;
    }
}
