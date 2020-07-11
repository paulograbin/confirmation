package com.paulograbin.confirmation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipationTest {

    @Test
    void participationIsCreatedWithoutId() {
        Participation p = new Participation();

        assertThat(p.getId()).isNull();
    }

    @Test
    void participationIsCreatedWithoutEvent() {
        Participation p = new Participation();

        assertThat(p.getEvent()).isNull();
    }

    @Test
    void participationIsCreatedWithoutUser() {
        Participation p = new Participation();

        assertThat(p.getUser()).isNull();
    }

    @Test
    void participationIsCreatedWithoutConfirmationDate() {
        Participation p = new Participation();

        assertThat(p.getConfirmationDate()).isNull();
    }

    @Test
    void participationIsCreatedAsConvited() {
        Participation p = new Participation();

        assertThat(p.getStatus()).isEqualByComparingTo(ParticipationStatus.CONVIDADO);
    }
//
//    @Test
//    void name() {
//        Event e = new Event();
//        User u = new User();
//
//        var p = new Participation();
//
//        p.setEvent(e);
//        p.setUser(u);
//
//        assertThat(e.getParticipants()).hasSize(1);
//        assertThat(u.getParticipations()).hasSize(1);
//    }
}
