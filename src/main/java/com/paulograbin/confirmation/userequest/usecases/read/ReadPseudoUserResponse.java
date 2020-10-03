package com.paulograbin.confirmation.userequest.usecases.read;

import lombok.Data;


@Data
public class ReadPseudoUserResponse {

    public boolean requestNotFound;
    public boolean successful;
    public String id;
    public String requestId;
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
    public boolean expired;
    public String errorMessage;
}
