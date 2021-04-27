package com.paulograbin.confirmation.participation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "event_id"})
})
public class Participation extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

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
        super(null);

        this.status = ParticipationStatus.CONVIDADO;
    }

    public Participation(User user, Event event) {
        super(null);

        this.user = user;
        this.event = event;
        this.invitationDate = DateUtils.getCurrentDate();
        this.status = ParticipationStatus.CONVIDADO;
    }

    public void inviteParticipant() {
        this.status = ParticipationStatus.CONVIDADO;
    }

    public void confirmParticipant() {
        this.status = ParticipationStatus.CONFIRMADO;
    }

    public void declineParticipant() {
        this.status = ParticipationStatus.RECUSADO;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", event=" + event.getTitle() +
                ", status=" + status +
                '}';
    }
}
