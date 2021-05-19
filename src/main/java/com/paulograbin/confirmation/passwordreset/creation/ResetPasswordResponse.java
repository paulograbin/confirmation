package com.paulograbin.confirmation.passwordreset.creation;

import lombok.Data;


@Data
public class ResetPasswordResponse {
    public boolean successful;
    public boolean invalidAddress;
}
