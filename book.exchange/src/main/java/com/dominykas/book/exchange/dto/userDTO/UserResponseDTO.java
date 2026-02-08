package com.dominykas.book.exchange.dto.userDTO;

import com.dominykas.book.exchange.entity.Notice;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
