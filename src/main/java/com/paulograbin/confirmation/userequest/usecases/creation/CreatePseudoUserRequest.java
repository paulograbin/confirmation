package com.paulograbin.confirmation.userequest.usecases.creation;

import lombok.Data;

@Data
public class CreatePseudoUserRequest {

    public long requestingUser;

    public String email;
    public String firstName;
    public String lastName;
    public long chapterId;

}
