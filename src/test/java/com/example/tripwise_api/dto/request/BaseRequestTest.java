package com.example.tripwise_api.dto.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseRequestTest {
    @Test
    void canSetAndGetFields() {
        BaseRequest req = new BaseRequest() {};
        req.setProvider("testProvider");
        req.setRawPayload("payload");
        assertEquals("testProvider", req.getProvider());
        assertEquals("payload", req.getRawPayload());
    }

    @Test
    void defaultFieldsShouldBeNull() {
        BaseRequest req = new BaseRequest() {};
        assertNull(req.getProvider());
        assertNull(req.getRawPayload());
    }

    @Test
    void canSetFieldsToNull() {
        BaseRequest req = new BaseRequest() {};
        req.setProvider(null);
        req.setRawPayload(null);
        assertNull(req.getProvider());
        assertNull(req.getRawPayload());
    }

    @Test
    void canSetFieldsToEmptyString() {
        BaseRequest req = new BaseRequest() {};
        req.setProvider("");
        req.setRawPayload("");
        assertEquals("", req.getProvider());
        assertEquals("", req.getRawPayload());
    }
}
