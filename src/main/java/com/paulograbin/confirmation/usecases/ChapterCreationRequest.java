package com.paulograbin.confirmation.usecases;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class ChapterCreationRequest {

    @NotNull
    @Getter
    @Setter
    private Long id;

    @NotBlank
    @Getter
    @Setter
    private String name;

    public ChapterCreationRequest() {
    }

    public ChapterCreationRequest(@NotNull Long id, @NotBlank String name) {
        this.id = id;
        this.name = name;
    }
}
