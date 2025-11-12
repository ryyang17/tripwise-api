package com.example.tripwise_api.integration;

import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.repository.TripRepository;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Database wordt automatisch gereset door @Transactional en create-drop
    }

    @AfterEach
    void tearDown() {
        // Database cleanup wordt automatisch gedaan door @Transactional rollback
        tripRepository.deleteAll();
    }

    @Test
    void createTrip_EndToEnd_ShouldPersistInDatabase() throws Exception {
        // Arrange
        CreateTripRequest request = new CreateTripRequest();
        request.setUserId(1L);
        request.setTripName("Integration Test Trip");
        request.setStartDate(LocalDate.of(2025, 6, 1));
        request.setEndDate(LocalDate.of(2025, 6, 5));
        request.setDestination("Amsterdam");
        request.setDescription("Full integration test");
        request.setTransportation("Train");

        // Act & Assert - API call
        mockMvc.perform(post("/api/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripName").value("Integration Test Trip"))
                .andExpect(jsonPath("$.destination").value("Amsterdam"))
                .andExpect(jsonPath("$.userId").value(1));

        // Assert - Database persistence
        List<Trip> tripsInDb = tripRepository.findAll();
        assertEquals(1, tripsInDb.size());

        Trip savedTrip = tripsInDb.get(0);
        assertEquals("Integration Test Trip", savedTrip.getTripName());
        assertEquals("Amsterdam", savedTrip.getDestination());
        assertEquals(1L, savedTrip.getUserId());
        assertNotNull(savedTrip.getCreatedDate());
    }

    @Test
    void getAllTrips_WithExistingData_ShouldReturnFromDatabase() throws Exception {
        // Arrange - Insert test data directly into database
        Trip trip1 = createTestTrip("Trip 1", "Amsterdam", 1L);
        Trip trip2 = createTestTrip("Trip 2", "Rotterdam", 2L);
        tripRepository.saveAll(List.of(trip1, trip2));

        // Act & Assert
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tripName", is("Trip 1")))
                .andExpect(jsonPath("$[1].tripName", is("Trip 2")));
    }

    @Test
    void getTripById_WithExistingTrip_ShouldReturnFromDatabase() throws Exception {
        // Arrange
        Trip savedTrip = tripRepository.save(createTestTrip("Test Trip", "Utrecht", 1L));

        // Act & Assert
        mockMvc.perform(get("/api/trips/{id}", savedTrip.getTripId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(savedTrip.getTripId()))
                .andExpect(jsonPath("$.tripName").value("Test Trip"))
                .andExpect(jsonPath("$.destination").value("Utrecht"));
    }

    @Test
    void updateTrip_EndToEnd_ShouldUpdateInDatabase() throws Exception {
        // Arrange
        Trip existingTrip = tripRepository.save(createTestTrip("Old Name", "Old City", 1L));

        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("Updated Name");
        updateRequest.setDestination("New City");
        updateRequest.setStartDate(LocalDate.of(2025, 7, 1));
        updateRequest.setEndDate(LocalDate.of(2025, 7, 5));
        updateRequest.setTransportation("Car");

        // Act & Assert - API call
        mockMvc.perform(put("/api/trips/{id}", existingTrip.getTripId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripName").value("Updated Name"))
                .andExpect(jsonPath("$.destination").value("New City"));

        // Assert - Database was updated
        Trip updatedTrip = tripRepository.findById(existingTrip.getTripId()).orElseThrow();
        assertEquals("Updated Name", updatedTrip.getTripName());
        assertEquals("New City", updatedTrip.getDestination());
        assertEquals("Car", updatedTrip.getTransportation());
    }

    @Test
    void deleteTrip_EndToEnd_ShouldRemoveFromDatabase() throws Exception {
        // Arrange
        Trip tripToDelete = tripRepository.save(createTestTrip("To Delete", "Somewhere", 1L));
        int tripId = tripToDelete.getTripId();

        // Verify trip exists
        assertTrue(tripRepository.existsById(tripId));

        // Act
        mockMvc.perform(delete("/api/trips/{id}", tripId))
                .andExpect(status().isNoContent());

        // Assert - Trip removed from database
        assertFalse(tripRepository.existsById(tripId));
        assertEquals(0, tripRepository.findAll().size());
    }

    @Test
    void getTripsByUserId_WithMultipleUsers_ShouldFilterCorrectly() throws Exception {
        // Arrange - Create trips for different users
        Trip user1Trip1 = createTestTrip("User 1 Trip 1", "City A", 1L);
        Trip user1Trip2 = createTestTrip("User 1 Trip 2", "City B", 1L);
        Trip user2Trip = createTestTrip("User 2 Trip", "City C", 2L);

        tripRepository.saveAll(List.of(user1Trip1, user1Trip2, user2Trip));

        // Act & Assert - Get trips for user 1
        mockMvc.perform(get("/api/trips").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is(1))))
                .andExpect(jsonPath("$[*].tripName", containsInAnyOrder("User 1 Trip 1", "User 1 Trip 2")));

        // Act & Assert - Get trips for user 2
        mockMvc.perform(get("/api/trips").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(2))
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
        request.setUserId(1L);
        request.setTripName("Test Transaction");
        request.setStartDate(LocalDate.of(2025, 8, 10)); // Start after end - should cause validation error
        request.setEndDate(LocalDate.of(2025, 8, 5));

        // Act
        mockMvc.perform(post("/api/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Assert - Transaction rolled back, no data persisted
        assertEquals(0, tripRepository.findAll().size());
    }

    private Trip createTestTrip(String name, String destination, Long userId) {
        Trip trip = new Trip();
        trip.setTripName(name);
        trip.setDestination(destination);
        trip.setUserId(userId);
        trip.setStartDate(LocalDate.of(2025, 6, 1));
        trip.setEndDate(LocalDate.of(2025, 6, 5));
        trip.setDescription("Test description");
        trip.setTransportation("Car");
        trip.setCreatedDate(LocalDateTime.now());
        return trip;
    }
}
