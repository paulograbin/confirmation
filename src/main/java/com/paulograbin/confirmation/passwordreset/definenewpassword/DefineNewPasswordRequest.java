package com.paulograbin.confirmation.passwordreset.definenewpassword;

import lombok.Data;
import lombok.ToString;


@Data
public class DefineNewPasswordRequest {

    private String requestCode;

    @ToString.Exclude
    private String password;
}
