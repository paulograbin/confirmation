package com.paulograbin.confirmation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String address;

    @OneToOne
    @Getter
    @Setter
    private User creator;

    @Getter
    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;

    @Getter
    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "event")
    @Getter
    @Setter
    private List<Participation> participants = new ArrayList<>();

    public Event() {
    }

    public Event(String title, String address, String description, User creator, LocalDateTime date) {
        this();

        this.title = title;
        this.address = address;
        this.description = description;
        this.creator = creator;
        this.dateTime = date;
        this.creationDate = LocalDateTime.now();

        Participation p = new Participation(creator, this);
        p.confirmParticipant();
        participants.add(p);
    }

    public void addParticipant(User participant) {
        Participation p = new Participation(participant, this);
        participants.add(p);
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", creator=" + creator.getUsername() +
                ", dateTime=" + dateTime +
                '}';
    }

    public void confirmParticipant(User firstInvited) {
        if (participants.contains(firstInvited)) {
            int i = participants.indexOf(firstInvited);
            Participation participation = participants.get(i);
            participation.confirmParticipant();
        }
    }
}
