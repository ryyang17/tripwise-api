package com.example.tripwise_api.integration;

import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.TripRepository;
import com.example.tripwise_api.repository.UserRepository;
import com.example.tripwise_api.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Transactional
class TripIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser1;
    private User testUser2;

    private String testUser1Token;
    private String testUser2Token;

    private Trip createTestTrip(String name, String destination, Long userId) {
        Trip trip = new Trip();
        trip.setTripName(name);
        trip.setDestination(destination);
        trip.setUserId(userId);
        trip.setStartDate(LocalDate.of(2025, 6, 1));
        trip.setEndDate(LocalDate.of(2025, 6, 5));
        trip.setDescription("Test description");
        trip.setTransportation("car");
        trip.setCreatedDate(LocalDateTime.now());
        return trip;
    }

    @BeforeEach
    void setUp() {
        // Create test users for trip creation
        testUser1 = new User("testuser1", "test1@example.com", "password123");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("testuser2", "test2@example.com", "password123");
        testUser2 = userRepository.save(testUser2);

        // Generate JWT tokens for test users
        testUser1Token = jwtUtil.generateAccessToken(testUser1.getUsername());
        testUser2Token = jwtUtil.generateAccessToken(testUser2.getUsername());
    }

    @AfterEach
    void tearDown() {
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createTrip_EndToEnd_ShouldPersistInDatabase() throws Exception {
        // Arrange
        CreateTripRequest request = new CreateTripRequest();
        request.setUserId(testUser1.getId());
        request.setTripName("Integration Test Trip");
        request.setStartDate(LocalDate.of(2025, 6, 1));
        request.setEndDate(LocalDate.of(2025, 6, 5));
        request.setDestination("Amsterdam");
        request.setDescription("Full integration test");
        request.setTransportation("train");

        // Act & Assert - API call
        mockMvc.perform(post("/api/trips")
                .header("Authorization", "Bearer " + testUser1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripName").value("Integration Test Trip"))
                .andExpect(jsonPath("$.destination").value("Amsterdam"))
                .andExpect(jsonPath("$.userId").value(testUser1.getId().intValue()));

        // Assert - Database persistence
        List<Trip> tripsInDb = tripRepository.findAll();
        assertEquals(1, tripsInDb.size());

        Trip savedTrip = tripsInDb.get(0);
        assertEquals("Integration Test Trip", savedTrip.getTripName());
        assertEquals("Amsterdam", savedTrip.getDestination());
        assertEquals(testUser1.getId(), savedTrip.getUserId());
        assertNotNull(savedTrip.getCreatedDate());
    }

    @Test
    void getAllTrips_WithExistingData_ShouldReturnFromDatabase() throws Exception {
        // Arrange - Insert test data directly into database
        Trip trip1 = createTestTrip("Trip 1", "Amsterdam", testUser1.getId());
        Trip trip2 = createTestTrip("Trip 2", "Rotterdam", testUser2.getId());
        tripRepository.saveAll(List.of(trip1, trip2));

        // Act & Assert
        mockMvc.perform(get("/api/trips")
                .header("Authorization", "Bearer " + testUser1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tripName", is("Trip 1")))
                .andExpect(jsonPath("$[1].tripName", is("Trip 2")));
    }

    @Test
    void getTripById_WithExistingTrip_ShouldReturnFromDatabase() throws Exception {
        // Arrange
        Trip savedTrip = tripRepository.save(createTestTrip("Test Trip", "Utrecht", testUser1.getId()));

        // Act & Assert
        mockMvc.perform(get("/api/trips/{id}", savedTrip.getTripId())
                .header("Authorization", "Bearer " + testUser1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(savedTrip.getTripId()))
                .andExpect(jsonPath("$.tripName").value("Test Trip"))
                .andExpect(jsonPath("$.destination").value("Utrecht"));
    }

    @Test
    void updateTrip_EndToEnd_ShouldUpdateInDatabase() throws Exception {
        // Arrange
        Trip existingTrip = tripRepository.save(createTestTrip("Old Name", "Old City", testUser1.getId()));

        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("Updated Name");
        updateRequest.setDestination("New City");
        updateRequest.setStartDate(LocalDate.of(2025, 7, 1));
        updateRequest.setEndDate(LocalDate.of(2025, 7, 5));
        updateRequest.setTransportation("car");
        updateRequest.setTripId(existingTrip.getTripId());
        updateRequest.setUserId(existingTrip.getUserId());

        // Act & Assert - API call
        mockMvc.perform(put("/api/trips/{id}", existingTrip.getTripId())
                .header("Authorization", "Bearer " + testUser1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripName").value("Updated Name"))
                .andExpect(jsonPath("$.destination").value("New City"));

        // Assert - Database was updated
        Trip updatedTrip = tripRepository.findById(existingTrip.getTripId()).orElseThrow();
        assertEquals("Updated Name", updatedTrip.getTripName());
        assertEquals("New City", updatedTrip.getDestination());
        assertEquals("car", updatedTrip.getTransportation());
    }

    @Test
    void deleteTrip_EndToEnd_ShouldRemoveFromDatabase() throws Exception {
        // Arrange
        Trip tripToDelete = tripRepository.save(createTestTrip("To Delete", "Somewhere", testUser1.getId()));
        int tripId = tripToDelete.getTripId();

        // Verify trip exists
        assertTrue(tripRepository.existsById(tripId));

        // Act
        mockMvc.perform(delete("/api/trips/{id}", tripId)
                .header("Authorization", "Bearer " + testUser1Token))
                .andExpect(status().isNoContent());

        // Assert - Trip removed from database
        assertFalse(tripRepository.existsById(tripId));
        assertEquals(0, tripRepository.findAll().size());
    }

    @Test
    void getTripsByUserId_WithMultipleUsers_ShouldFilterCorrectly() throws Exception {
        // Arrange - Create trips for different users
        Trip user1Trip1 = createTestTrip("User 1 Trip 1", "City A", testUser1.getId());
        Trip user1Trip2 = createTestTrip("User 1 Trip 2", "City B", testUser1.getId());
        Trip user2Trip = createTestTrip("User 2 Trip", "City C", testUser2.getId());

        tripRepository.saveAll(List.of(user1Trip1, user1Trip2, user2Trip));

        // Act & Assert - Get trips for user 1
        mockMvc.perform(get("/api/trips").param("userId", testUser1.getId().toString())
                .header("Authorization", "Bearer " + testUser1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is(testUser1.getId().intValue()))))
                .andExpect(jsonPath("$[*].tripName", containsInAnyOrder("User 1 Trip 1", "User 1 Trip 2")));

        // Act & Assert - Get trips for user 2
        mockMvc.perform(get("/api/trips").param("userId", testUser2.getId().toString())
                .header("Authorization", "Bearer " + testUser2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(testUser2.getId().intValue()))
                .andExpect(jsonPath("$[0].tripName").value("User 2 Trip"));
    }

    @Test
    void createTrip_WithInvalidData_ShouldReturnValidationError() throws Exception {
        // Arrange - Invalid request with missing required fields
        CreateTripRequest invalidRequest = new CreateTripRequest();
        invalidRequest.setUserId(null); // Missing required field
        invalidRequest.setTripName(""); // Empty name

        // Act & Assert
        mockMvc.perform(post("/api/trips")
                .header("Authorization", "Bearer " + testUser1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Assert - No data was persisted
        assertEquals(0, tripRepository.findAll().size());
    }

    @Test
    void databaseTransactionRollback_OnError_ShouldNotPersistData() throws Exception {
        // Arrange
        CreateTripRequest request = new CreateTripRequest();
        request.setUserId(testUser1.getId());
        request.setTripName("Test Transaction");
        request.setStartDate(LocalDate.of(2025, 8, 10)); // Start after end - should cause validation error
        request.setEndDate(LocalDate.of(2025, 8, 5));

        // Act
        mockMvc.perform(post("/api/trips")
                .header("Authorization", "Bearer " + testUser1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Assert
        assertEquals(0, tripRepository.findAll().size());
    }


}
