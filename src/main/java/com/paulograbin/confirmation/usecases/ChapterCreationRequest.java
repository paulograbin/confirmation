package com.paulograbin.confirmation.usecases;

import lombok.Getter;
import lombok.Setter;


public class ChapterCreationRequest {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    public ChapterCreationRequest() {
    }

    public ChapterCreationRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
