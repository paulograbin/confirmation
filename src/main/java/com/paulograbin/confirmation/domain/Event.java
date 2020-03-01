package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private LocalDateTime dateTime;

    @Getter
    @Setter
    private LocalDateTime creationDate;

    @ManyToOne
    @Getter
    @Setter
    private Chapter chapter;

    @Getter
    @Setter
    private boolean published;

    @OneToMany(mappedBy = "event")
    @Getter
    @Setter
    private List<Participation> participants = new ArrayList<>();

    public Event() {
    }

    public Event(Chapter chapter, String title, String address, String description, User creator, LocalDateTime date) {
        this.chapter = chapter;
        this.title = title;
        this.address = address;
        this.description = description;
        this.creator = creator;
        this.dateTime = date;
        this.creationDate = LocalDateTime.now();
        this.published = false;

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
}
