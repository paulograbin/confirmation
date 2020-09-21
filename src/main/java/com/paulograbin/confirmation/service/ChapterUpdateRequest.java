package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.User;
import lombok.Getter;
import lombok.Value;


@Value
public class ChapterUpdateRequest {

    public ChapterUpdateRequest(Long id, User master) {
        this.id = id;
        this.master = master;
    }

    @Getter
    Long id;

    @Getter
    User master;

}
