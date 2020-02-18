package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.util.List;


@Data
public class EventDetailsDTO extends EventDTO {

    private List<ParticipationWithoutEventDTO> participants;

}
