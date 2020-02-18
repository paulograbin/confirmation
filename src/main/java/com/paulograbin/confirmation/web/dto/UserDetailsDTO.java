package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.util.List;


@Data
public class UserDetailsDTO extends UserDTO {

    private List<ParticipationWithoutUserDTO> participations;

}
