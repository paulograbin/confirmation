package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.User;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ChapterUpdateRequest {

    public ChapterUpdateRequest(@NotNull Long id, @NotNull User master) {
        this.id = id;
        this.master = master;
    }

    @NotNull
    @Getter
    Long id;

    @NotNull
    @Getter
    User master;

}
