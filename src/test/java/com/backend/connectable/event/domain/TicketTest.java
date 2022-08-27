package com.backend.connectable.event.domain;

import com.backend.connectable.exception.ConnectableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class TicketTest {

    @DisplayName("새로 생성한 티켓은 isUsed가 false이다.")
    @Test
    void isUsedFalseWhenCreated() {
        // given
        Ticket ticket = Ticket.builder()
            .tokenId(1)
            .tokenUri("tokenUri")
            .price(10000)
            .build();

        // when & then
        assertThat(ticket.isUsed()).isFalse();
    }

    @DisplayName("티켓의 isUsed가 false여야 사용할 수 있다.")
    @Test
    void useTicket() {
        // given
        Ticket ticket = Ticket.builder()
            .tokenId(1)
            .tokenUri("tokenUri")
            .price(10000)
            .build();

        // when & then
        assertThatCode(ticket::useToEnterEvent).doesNotThrowAnyException();
    }

    @DisplayName("티켓의 isUsed가 true라면 재입장에 사용할 수 있다.")
    @Test
    void usedTicketCannotBeReused() {
        // given
        Ticket ticket = Ticket.builder()
            .tokenId(1)
            .tokenUri("tokenUri")
            .price(10000)
            .build();
        ticket.useToEnterEvent();

        // when & then
        assertThatCode(ticket::useToEnterEvent)
            .isInstanceOf(ConnectableException.class);
    }
}