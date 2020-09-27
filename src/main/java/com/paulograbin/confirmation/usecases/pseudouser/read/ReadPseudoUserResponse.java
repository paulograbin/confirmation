package com.paulograbin.confirmation.usecases.pseudouser.read;

import lombok.Data;

import java.util.UUID;


@Data
public class ReadPseudoUserResponse {

    public boolean requestNotFound;
    public boolean successful;
    public UUID requestId;
    public String firstName;
    public String lastName;
    public String email;
    public String creationDate;
    public String expirationDate;
    public String conversionDate;

    public Long creatorId;
    public String creator;

    public Long chapterId;
    public String chapterName;

    public Long userId;
    public String username;
}
