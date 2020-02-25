package com.paulograbin.confirmation.web;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
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
