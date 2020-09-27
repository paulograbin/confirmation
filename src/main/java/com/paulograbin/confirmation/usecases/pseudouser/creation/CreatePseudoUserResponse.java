package com.paulograbin.confirmation.usecases.pseudouser.creation;

import lombok.Data;

import java.util.UUID;


@Data
public class CreatePseudoUserResponse {

    public String errorMessage;
    public UUID requestNumber;
    public boolean successful;
    public boolean notAllowed;
    public boolean emailNotAvailable;
    public boolean invalidRequestingUser;

    public boolean invalidFirstName;
    public boolean invalidLastName;
    public boolean invalidEmail;
}
