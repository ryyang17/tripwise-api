package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Trip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TripRepositoryTest {
    @Autowired
    private TripRepository tripRepository;

    @Test
    void testSaveAndFindTrip() {
        Trip trip = new Trip();
        trip.setUserId(2L);
        trip.setTripName("IntegrationTestTrip");
        trip.setStartDate(LocalDate.of(2025, 2, 1));
        trip.setEndDate(LocalDate.of(2025, 2, 5));
        trip.setDestination("Berlin");
        trip.setDescription("Integration test description");
        trip.setTransportation("Train");
        trip.setCreatedDate(LocalDateTime.now());

        Trip savedTrip = tripRepository.save(trip);
        assertNotNull(savedTrip.getTripId());

        Trip foundTrip = tripRepository.findById(savedTrip.getTripId()).orElse(null);
        assertNotNull(foundTrip);
        assertEquals("Berlin", foundTrip.getDestination());
        assertEquals("IntegrationTestTrip", foundTrip.getTripName());
    }
}

