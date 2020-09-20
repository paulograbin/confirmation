package com.paulograbin.confirmation.usecases.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserRequestAdmin {

    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Boolean master;

    @NotNull
    private Boolean active;

    private String password;

}
