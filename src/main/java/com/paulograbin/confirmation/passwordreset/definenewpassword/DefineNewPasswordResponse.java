package com.paulograbin.confirmation.passwordreset.definenewpassword;

import lombok.Data;


@Data
public class DefineNewPasswordResponse {

    public boolean successful;
    public boolean invalidRequestCode;
    public boolean invalidPassword;
    public boolean nonExistingRequest;
    public boolean userNotFound;
    public String errorMessage;
}
