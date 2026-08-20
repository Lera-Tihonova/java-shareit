package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertThat(json.write(dto)).hasJsonPathNumberValue("$.itemId");
    }
}