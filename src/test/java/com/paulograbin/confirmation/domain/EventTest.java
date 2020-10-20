package com.paulograbin.confirmation.domain;

import com.paulograbin.confirmation.event.Event;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class EventTest {

    @Test
    void newEventHasEmptyId() {
        Event e = new Event();

        assertThat(e.getId()).isNull();
    }

    @Test
    void eventIsCreatedWithoutParticipations() {
        var e = new Event();

        assertThat(e.getParticipants()).isEmpty();
    }

    @Test
    void eventIsCreatedUnpublished() {
        Event e = new Event();

        assertThat(e.isPublished()).isFalse();
    }
}
