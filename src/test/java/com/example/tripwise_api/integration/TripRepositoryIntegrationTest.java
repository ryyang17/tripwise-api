package com.example.tripwise_api.integration;

import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.repository.TripRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("integration-test")
class TripRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripRepository tripRepository;

    private Trip testTrip1;
    private Trip testTrip2;
    private Trip testTrip3;

    @BeforeEach
    void setUp() {
        // Create test data
        testTrip1 = createTrip("Amsterdam Trip", "Amsterdam", 1L);
        testTrip2 = createTrip("Rotterdam Trip", "Rotterdam", 1L);
        testTrip3 = createTrip("Utrecht Trip", "Utrecht", 2L);
    }

    @AfterEach
    void tearDown() {
        // @DataJpaTest zorgt automatisch voor cleanup tussen tests
        tripRepository.deleteAll();
        entityManager.clear();
    }

    @Test
    void findByUserId_WithExistingUser_ShouldReturnUserTrips() {
        // Arrange
        entityManager.persistAndFlush(testTrip1);
        entityManager.persistAndFlush(testTrip2);
        entityManager.persistAndFlush(testTrip3);

        // Act
        List<Trip> user1Trips = tripRepository.findByUserId(1L);
        List<Trip> user2Trips = tripRepository.findByUserId(2L);

        // Assert
        assertEquals(2, user1Trips.size());
        assertEquals(1, user2Trips.size());

        assertTrue(user1Trips.stream().allMatch(trip -> trip.getUserId().equals(1L)));
        assertTrue(user2Trips.stream().allMatch(trip -> trip.getUserId().equals(2L)));
    }

    @Test
    void findByUserId_WithNonExistingUser_ShouldReturnEmptyList() {
        // Arrange
        entityManager.persistAndFlush(testTrip1);

        // Act
        List<Trip> trips = tripRepository.findByUserId(999L);

        // Assert
        assertTrue(trips.isEmpty());
    }

    @Test
    void save_WithValidTrip_ShouldPersistToDatabase() {
        // Arrange
        Trip newTrip = createTrip("New Trip", "Eindhoven", 3L);

        // Act
        Trip savedTrip = tripRepository.save(newTrip);
        entityManager.flush();
        entityManager.clear(); // Clear the persistence context

        // Assert
        assertNotNull(savedTrip.getTripId());

        Optional<Trip> retrievedTrip = tripRepository.findById(savedTrip.getTripId());
        assertTrue(retrievedTrip.isPresent());
        assertEquals("New Trip", retrievedTrip.get().getTripName());
        assertEquals("Eindhoven", retrievedTrip.get().getDestination());
        assertEquals(3L, retrievedTrip.get().getUserId());
    }

    @Test
    void findById_WithExistingId_ShouldReturnTrip() {
        // Arrange
        Trip persistedTrip = entityManager.persistAndFlush(testTrip1);
        entityManager.clear();

        // Act
        Optional<Trip> foundTrip = tripRepository.findById(persistedTrip.getTripId());

        // Assert
        assertTrue(foundTrip.isPresent());
        assertEquals(testTrip1.getTripName(), foundTrip.get().getTripName());
        assertEquals(testTrip1.getDestination(), foundTrip.get().getDestination());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Trip> foundTrip = tripRepository.findById(999);

        // Assert
        assertFalse(foundTrip.isPresent());
    }

    @Test
    void deleteById_WithExistingTrip_ShouldRemoveFromDatabase() {
        // Arrange
        Trip persistedTrip = entityManager.persistAndFlush(testTrip1);
        Integer tripId = persistedTrip.getTripId();

        // Verify trip exists
        assertTrue(tripRepository.existsById(tripId));

        // Act
        tripRepository.deleteById(tripId);
        entityManager.flush();

        // Assert
        assertFalse(tripRepository.existsById(tripId));
        Optional<Trip> deletedTrip = tripRepository.findById(tripId);
        assertFalse(deletedTrip.isPresent());
    }

    @Test
    void findAll_WithMultipleTrips_ShouldReturnAllTrips() {
        // Arrange
        entityManager.persistAndFlush(testTrip1);
        entityManager.persistAndFlush(testTrip2);
        entityManager.persistAndFlush(testTrip3);

        // Act
        List<Trip> allTrips = tripRepository.findAll();

        // Assert
        assertEquals(3, allTrips.size());
    }

    @Test
    void existsById_WithExistingTrip_ShouldReturnTrue() {
        // Arrange
        Trip persistedTrip = entityManager.persistAndFlush(testTrip1);

        // Act
        boolean exists = tripRepository.existsById(persistedTrip.getTripId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_WithNonExistingTrip_ShouldReturnFalse() {
        // Act
        boolean exists = tripRepository.existsById(999);

        // Assert
        assertFalse(exists);
    }

    @Test
    void updateTrip_ShouldPersistChanges() {
        // Arrange
        Trip persistedTrip = entityManager.persistAndFlush(testTrip1);
        entityManager.clear();

        // Act - Update the trip
        Trip tripToUpdate = tripRepository.findById(persistedTrip.getTripId()).orElseThrow();
        tripToUpdate.setTripName("Updated Trip Name");
        tripToUpdate.setDestination("Updated Destination");
        Trip updatedTrip = tripRepository.save(tripToUpdate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Trip retrievedTrip = tripRepository.findById(updatedTrip.getTripId()).orElseThrow();
        assertEquals("Updated Trip Name", retrievedTrip.getTripName());
        assertEquals("Updated Destination", retrievedTrip.getDestination());
    }

    @Test
    void databaseConstraints_ShouldWorkCorrectly() {
        // Test that we can save trips with the same name but different users
        Trip trip1 = createTrip("Same Name", "City1", 1L);
        Trip trip2 = createTrip("Same Name", "City2", 2L);

        // Act
        Trip saved1 = tripRepository.save(trip1);
        Trip saved2 = tripRepository.save(trip2);
        entityManager.flush();

        // Assert
        Integer tripId1 = saved1.getTripId();
        Integer tripId2 = saved2.getTripId();
        assertNotNull(tripId1);
        assertNotNull(tripId2);
        assertNotEquals(tripId1, tripId2);
    }

    private Trip createTrip(String name, String destination, Long userId) {
        Trip trip = new Trip();
        trip.setTripName(name);
        trip.setDestination(destination);
        trip.setUserId(userId);
        trip.setStartDate(LocalDate.of(2025, 6, 1));
        trip.setEndDate(LocalDate.of(2025, 6, 5));
        trip.setDescription("Test description for " + name);
        trip.setTransportation("Car");
        trip.setCreatedDate(LocalDateTime.now());
        return trip;
    }
}
