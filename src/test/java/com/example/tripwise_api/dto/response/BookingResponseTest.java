package com.example.tripwise_api.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingResponseTest {
    @Test
    void canInstantiate() {
        BookingResponse resp = new BookingResponse();
        assertNotNull(resp);
    }
}

