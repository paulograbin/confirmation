package com.paulograbin.confirmation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "event_id"})
})
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Getter
    @Setter
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @Getter
    @Setter
    private Event event;

    @Getter
    @Setter
    private ParticipationStatus status;

    @Getter
    @Setter
    private LocalDateTime invitationDate;

    @Getter
    @Setter
    private LocalDateTime confirmationDate;

    public Participation() {
    }

    public Participation(User user, Event event) {
        this.user = user;
        this.event = event;
        this.invitationDate = LocalDateTime.now();
        this.status = ParticipationStatus.INVITED;
    }

    public void confirmParticipant() {
        this.status = ParticipationStatus.CONFIRMED;
    }


    @Override
    public String toString() {
        return "Participation{" +
                "user=" + user.getUsername() +
                ", event=" + event.getTitle() +
                ", status=" + status +
                '}';
    }
}
