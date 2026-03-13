package com.dominykas.book.exchange.dto.userDTO;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {

    private String email;
    private String firstName;
    private String lastName;
}