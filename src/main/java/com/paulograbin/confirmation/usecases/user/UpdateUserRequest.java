package com.paulograbin.confirmation.usecases.user;

import lombok.Data;
import lombok.ToString;

@Data
public class UpdateUserRequest {

    private Long id;
    private String firstName;
    private String lastName;

    @ToString.Exclude
    private String password;

}
