package com.paulograbin.confirmation.event;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.participation.Participation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
public class Event extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(length = 500)
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
    private Set<Participation> participants = new HashSet<>();


    public Event(Chapter chapter, String title, String address, String description, User creator, LocalDate date, LocalTime time) {
        super(null);

        this.chapter = chapter;
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.time = time;
        this.creationDate = DateUtils.getCurrentDate();
        this.published = false;

        addUserAsCreator(creator);
        addUserAsParticipant(creator);
    }

    public Event() {
        super(null);
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
