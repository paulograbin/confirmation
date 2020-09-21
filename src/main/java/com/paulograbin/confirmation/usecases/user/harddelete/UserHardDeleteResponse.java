package com.paulograbin.confirmation.usecases.user.harddelete;

import lombok.Data;

@Data
public class UserHardDeleteResponse {

    public String errorMessage;
    public boolean userNotFound;
    public boolean successful;
    public boolean notAdmin;
}
