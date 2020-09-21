package com.paulograbin.confirmation.usecases.user;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private Long id;

    private String firstName;

    private String lastName;
    private String password;

}
