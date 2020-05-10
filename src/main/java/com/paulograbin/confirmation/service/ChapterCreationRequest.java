package com.paulograbin.confirmation.service;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Value
public class ChapterCreationRequest {

    @NotNull
    @Getter
    @Setter
    private Long id;

    @NotBlank
    @Getter
    @Setter
    private String name;

}
