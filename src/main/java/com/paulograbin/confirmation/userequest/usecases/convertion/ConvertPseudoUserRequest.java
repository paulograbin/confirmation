package com.paulograbin.confirmation.userequest.usecases.convertion;

import lombok.Data;


@Data
public class ConvertPseudoUserRequest {

    public String requestNumber;
    public String username;
    public String firstName;
    public String lastName;
    public String password;

}
