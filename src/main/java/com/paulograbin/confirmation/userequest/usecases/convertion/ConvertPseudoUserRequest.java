package com.paulograbin.confirmation.userequest.usecases.convertion;

import lombok.Data;
 import lombok.ToString;


@Data
public class ConvertPseudoUserRequest {

    public String requestNumber;
    public String username;
    public String firstName;
    public String lastName;

    @ToString.Exclude
    public String password;

}
