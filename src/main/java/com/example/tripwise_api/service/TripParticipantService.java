package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripParticipantDAL;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.repository.TripParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripParticipantService {
    private final TripParticipantDAL dal;
    private final TripParticipantRepository repository;

    public TripParticipantService(TripParticipantDAL dal, TripParticipantRepository repository) {
        this.dal = dal;
        this.repository = repository;
    }

    public TripParticipant addParticipant(TripParticipant participant) {
        return dal.saveParticipant(participant);
    }

    public List<TripParticipant> getParticipantsByTripId(Integer tripId) {
        return dal.getParticipantsByTripId(tripId);
    }

    // Get participants with user details efficiently loaded
    @Transactional(readOnly = true)
    public List<TripParticipant> getParticipantsByTripIdWithUserDetails(Integer tripId) {
        return repository.findByTripIdWithUser(tripId);
    }

    public Optional<TripParticipant> getParticipantById(Integer id) {
        return dal.getParticipantById(id);
    }

    // Validate multiple users are participants (for shared expenses)
    @Transactional(readOnly = true)
    public void validateUsersAreParticipants(Integer tripId, List<Long> userIds) {
        for (Long userId : userIds) {
            if (!repository.existsByTripIdAndUserId(tripId, userId)) {
                throw new IllegalArgumentException("User " + userId + " is not a participant of trip " + tripId);
            }
        }
    }

    public TripParticipant updateParticipant(Integer participantId, String role, Boolean isOwner) {
        Optional<TripParticipant> existingParticipant = dal.getParticipantById(participantId);

        if (existingParticipant.isEmpty()) {
            throw new IllegalArgumentException("Participant not found with id: " + participantId);
        }

        TripParticipant participant = existingParticipant.get();

        // If isOwner is true, set role to "owner", otherwise use the provided role
        if (isOwner != null && isOwner) {
            participant.setRole("owner");
        } else {
            participant.setRole(role);
        }

        // Save and then return with user details properly loaded
        TripParticipant savedParticipant = dal.saveParticipant(participant);

        // Fetch the updated participant with user details to avoid lazy loading issues
        return repository.findByTripIdWithUser(savedParticipant.getTripId())
                .stream()
                .filter(p -> p.getId().equals(participantId))
                .findFirst()
                .orElse(savedParticipant);
    }

    public void removeParticipant(Integer id) {
        dal.deleteParticipant(id);
    }

    public void deleteParticipantsByTripId(Integer tripId) {
        dal.deleteParticipantsByTripId(tripId);
    }
}
