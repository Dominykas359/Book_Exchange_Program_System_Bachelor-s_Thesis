package com.dominykas.book.exchange.dto.userDTO;

import lombok.Data;

@Data
public class UserRequestDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
