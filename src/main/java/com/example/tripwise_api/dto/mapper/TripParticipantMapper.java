package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateParticipantRequest;
import com.example.tripwise_api.dto.response.TripParticipantResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.TripService;
import com.example.tripwise_api.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper voor het converteren tussen TripParticipant entities en DTOs
 */
@Component
public class TripParticipantMapper {

    private final UserService userService;
    private final TripService tripService;

    public TripParticipantMapper(UserService userService, TripService tripService) {
        this.userService = userService;
        this.tripService = tripService;
    }

    /**
     * Convert TripParticipant entity naar TripParticipantResponse DTO
     * STAP 3: Vult BEIDE id en participantId velden voor backwards compatibility
     */
    public TripParticipantResponse toResponse(TripParticipant participant) {
        if (participant == null) return null;

        TripParticipantResponse response = new TripParticipantResponse();

        // BELANGRIJK: Beide ID velden vullen voor frontend compatibility
        response.setParticipantId(participant.getId());
        response.setTripId(participant.getTripId());
        response.setUserId(participant.getUserId());
        response.setRole(participant.getRole());
        response.setJoinedDate(participant.getJoinedDate());

        // Fetch user details via UserService
        Optional<User> userOpt = userService.getUserById(participant.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.setUserName(user.getUsername());
            response.setUserEmail(user.getEmail());
        }

        // Determine if participant is owner by comparing with trip owner
        Optional<Trip> tripOpt = tripService.getTripById(participant.getTripId());
        if (tripOpt.isPresent()) {
            Trip trip = tripOpt.get();
            response.setIsOwner(participant.getUserId().equals(trip.getUserId()));
        } else {
            response.setIsOwner(false);
        }

        return response;
    }

    /**
     * Convert lijst van TripParticipant entities naar lijst van TripParticipantResponse DTOs
     */
    public List<TripParticipantResponse> toResponseList(List<TripParticipant> participants) {
        if (participants == null) return null;
        return participants.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert CreateParticipantRequest naar TripParticipant entity
     */
    public static TripParticipant toEntity(CreateParticipantRequest request, Integer tripId) {
        if (request == null) return null;

        TripParticipant participant = new TripParticipant();
        participant.setTripId(tripId);
        participant.setUserId(request.getUserId());
        participant.setRole(request.getRole());

        // userName, userEmail, isOwner worden niet opgeslagen in participant entity
        // maar kunnen worden gebruikt voor validatie of logging

        return participant;
    }
}
