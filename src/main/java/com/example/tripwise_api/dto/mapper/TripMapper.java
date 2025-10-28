package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.dto.response.TripResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper voor het converteren tussen Trip entities en DTOs
 */
@Component
public class TripMapper {

    private final UserService userService;

    public TripMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Convert Trip entity naar TripResponse DTO
     */
    public TripResponse toResponse(Trip trip) {
        if (trip == null) return null;

        TripResponse response = new TripResponse();
        response.setTripId(trip.getTripId());
        response.setUserId(trip.getUserId());
        response.setTripName(trip.getTripName());
        response.setStartDate(trip.getStartDate());
        response.setEndDate(trip.getEndDate());
        response.setDestination(trip.getDestination());
        response.setDescription(trip.getDescription());
        response.setTransportation(trip.getTransportation());
        response.setCreatedDate(trip.getCreatedDate());

        // Bereken isActive op basis van endDate >= today
        response.setIsActive(trip.getEndDate() != null &&
                           !trip.getEndDate().isBefore(LocalDate.now()));

        // Fetch owner details if needed
        if (trip.getUserId() != null) {
            Optional<User> userOpt = userService.getUserById(trip.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.setOwnerName(user.getUsername());
                response.setOwnerEmail(user.getEmail());
            }
        }

        return response;
    }

    /**
     * Convert lijst van Trip entities naar lijst van TripResponse DTOs
     */
    public List<TripResponse> toResponseList(List<Trip> trips) {
        if (trips == null) return null;
        return trips.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert CreateTripRequest naar Trip entity
     */
    public static Trip toEntity(CreateTripRequest request) {
        if (request == null) return null;

        Trip trip = new Trip();
        trip.setUserId(request.getUserId());
        trip.setTripName(request.getTripName());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setTransportation(request.getTransportation());

        return trip;
    }

    /**
     * Update existing Trip entity met UpdateTripRequest data
     */
    public static void updateEntity(Trip trip, UpdateTripRequest request) {
        if (trip == null || request == null) return;

        if (request.getTripName() != null) {
            trip.setTripName(request.getTripName());
        }
        if (request.getStartDate() != null) {
            trip.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            trip.setEndDate(request.getEndDate());
        }
        if (request.getDestination() != null) {
            trip.setDestination(request.getDestination());
        }
        if (request.getDescription() != null) {
            trip.setDescription(request.getDescription());
        }
        if (request.getTransportation() != null) {
            trip.setTransportation(request.getTransportation());
        }
    }
}
