package com.paulograbin.confirmation.userequest.usecases.convertion;

import lombok.Data;


@Data
public class ConvertPseudoUserResponse {

    public String requestNumber;
    public boolean successful;
    public String userId;
    public String username;
    public String email;

    public String errorMessage;
    public boolean requestNotFound;
    public boolean expired;
    public boolean usernameNotAvailable;
}
