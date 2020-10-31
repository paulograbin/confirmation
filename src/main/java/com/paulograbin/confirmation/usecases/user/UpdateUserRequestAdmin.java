package com.paulograbin.confirmation.usecases.user;

import lombok.Data;
import lombok.ToString;

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

    @ToString.Exclude
    private String password;

}
