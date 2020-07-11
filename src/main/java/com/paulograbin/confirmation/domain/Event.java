package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String address;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime creationDate;
    private boolean published;

    @OneToOne
    private User creator;

    @ManyToOne
    private Chapter chapter;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<Participation> participants = new ArrayList<>();


    public Event(Chapter chapter, String title, String address, String description, User creator, LocalDate date, LocalTime time) {
        this.chapter = chapter;
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.time = time;
        this.creationDate = LocalDateTime.now();
        this.published = false;

        addUserAsCreator(creator);
        addUserAsParticipant(creator);
    }

    private void addUserAsParticipant(User creator) {
        Participation p = new Participation(creator, this);
        p.confirmParticipant();
        participants.add(p);
    }

    public void addUserAsCreator(final User creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator.getId() +
                ", date=" + date +
                ", time=" + time +
                ", participants=" + participants.size() +
                '}';
    }
}
