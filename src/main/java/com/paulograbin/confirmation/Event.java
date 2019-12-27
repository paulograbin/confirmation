package com.paulograbin.confirmation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String address;

    @OneToOne
    private User creator;

    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime dateTime;

    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "event")
    @Getter
    @Setter
    private List<Participation> participants = new ArrayList<>();

    public Event() {
    }

    public Event(String title, String address, User creator, LocalDateTime date) {
        this();

        this.title = title;
        this.address = address;
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

    public List<Participation> getParticipants() {
        return participants;
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

    public void setId(Long id) {
        this.id = id;
    }
}
