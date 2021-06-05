package com.paulograbin.confirmation.passwordreset.read;

import lombok.Data;

import java.util.UUID;


@Data
public class ReadPasswordResetResponse {

    public boolean successful;
    public boolean invalidCode;
    public String errorMessage;
    public boolean notFoundOrExpired;
    public UUID requestCode;
}
