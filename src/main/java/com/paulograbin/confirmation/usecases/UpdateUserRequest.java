package com.paulograbin.confirmation.usecases;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String password;

}
