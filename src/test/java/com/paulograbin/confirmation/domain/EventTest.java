package com.paulograbin.confirmation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void newEventHasEmptyId() {
        Event e = new Event();

        assertThat(e.getId()).isNull();
    }
}
