package com.example.tripwise_api.integration;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.UserRepository;
import com.example.tripwise_api.repository.BookingRepository;
import com.example.tripwise_api.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Transactional
class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("bookinguser", "booking@test.com", passwordEncoder.encode("password123"));
        testUser = userRepository.save(testUser);

        accessToken = jwtUtil.generateAccessToken(testUser.getUsername());
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        Map<String, Object> bookingRequest = new HashMap<>();
        bookingRequest.put("bookingType", "FLIGHT");
        bookingRequest.put("provider", "Amadeus");
        bookingRequest.put("bookingReference", "ABC123");
        bookingRequest.put("price", 299.99);
        bookingRequest.put("currency", "EUR");
        bookingRequest.put("searchParams", "{\"origin\":\"AMS\",\"destination\":\"BCN\"}");
        bookingRequest.put("rawPayload", "{\"data\":{\"flight\":\"details\"}}");

        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookingType").value("FLIGHT"))
                .andExpect(jsonPath("$.provider").value("Amadeus"))
                .andExpect(jsonPath("$.bookingReference").value("ABC123"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void testCreateBooking_Unauthorized() throws Exception {
        Map<String, Object> bookingRequest = new HashMap<>();
        bookingRequest.put("bookingType", "HOTEL");
        bookingRequest.put("provider", "Amadeus");
        bookingRequest.put("rawPayload", "{\"data\":{\"hotel\":\"details\"}}");

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isForbidden()); // Spring Security returns 403 when no auth provided
    }

    @Test
    void testCreateBooking_MissingRawPayload() throws Exception {
        Map<String, Object> bookingRequest = new HashMap<>();
        bookingRequest.put("bookingType", "FLIGHT");
        bookingRequest.put("provider", "Amadeus");

        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingsForCurrentUser_Success() throws Exception {
        // Create a booking first
        Map<String, Object> bookingRequest = new HashMap<>();
        bookingRequest.put("bookingType", "FLIGHT");
        bookingRequest.put("provider", "Amadeus");
        bookingRequest.put("rawPayload", "{\"data\":{\"flight\":\"details\"}}");

        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());

        // Get bookings
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].bookingType").value("FLIGHT"))
                .andExpect(jsonPath("$[0].provider").value("Amadeus"));
    }

    @Test
    void testGetBookingsForCurrentUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isForbidden()); // Spring Security returns 403 when no auth provided
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        // Create a booking first
        Map<String, Object> bookingRequest = new HashMap<>();
        bookingRequest.put("bookingType", "HOTEL");
        bookingRequest.put("provider", "Amadeus");
        bookingRequest.put("bookingReference", "HOTEL456");
        bookingRequest.put("rawPayload", "{\"data\":{\"hotel\":\"details\"}}");

        String response = mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> createdBooking = objectMapper.readValue(response, Map.class);
        Integer bookingId = (Integer) createdBooking.get("id");

        // Get booking by ID
        mockMvc.perform(get("/api/bookings/" + bookingId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.bookingType").value("HOTEL"))
                .andExpect(jsonPath("$.bookingReference").value("HOTEL456"));
    }

    @Test
    void testGetBookingById_NotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/99999")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}
