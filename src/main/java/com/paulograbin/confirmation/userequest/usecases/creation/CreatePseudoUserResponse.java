package com.paulograbin.confirmation.userequest.usecases.creation;

import lombok.Data;


@Data
public class CreatePseudoUserResponse {

    public String errorMessage;
    public String requestNumber;
    public boolean successful;
    public boolean notAllowed;
    public boolean emailNotAvailable;
    public boolean invalidRequestingUser;

    public boolean invalidFirstName;
    public boolean invalidLastName;
    public boolean invalidEmail;
}
