package com.paulograbin.confirmation.usecases.user.harddelete;

import lombok.Data;

@Data
public class UserHardDeleteRequest {

    private long userToDeleteId;
    private Long requestingUser;

}
