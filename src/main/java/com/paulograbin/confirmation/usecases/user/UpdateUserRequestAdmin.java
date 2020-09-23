package com.paulograbin.confirmation.usecases.user;

import lombok.Data;

@Data
public class UpdateUserRequestAdmin {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private long chapter;
    private Boolean master;
    private Boolean active;
    private String password;

}
