package com.paulograbin.confirmation.usecases;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserRequest {

    @NotNull
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
    private String password;

}
