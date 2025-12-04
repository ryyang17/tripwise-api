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

    // New method: Get participants with user details efficiently loaded
    @Transactional(readOnly = true)
    public List<TripParticipant> getParticipantsByTripIdWithUserDetails(Integer tripId) {
        return repository.findByTripIdWithUser(tripId);
    }

    public Optional<TripParticipant> getParticipantById(Integer id) {
        return dal.getParticipantById(id);
    }

    // New method: Check if user is participant of trip (for shared expense validation)
    @Transactional(readOnly = true)
    public boolean isUserParticipantOfTrip(Integer tripId, Long userId) {
        return repository.existsByTripIdAndUserId(tripId, userId);
    }

    // New method: Get participant by trip and user (for shared expense creation)
    @Transactional(readOnly = true)
    public Optional<TripParticipant> getParticipantByTripAndUser(Integer tripId, Long userId) {
        return repository.findByTripIdAndUserId(tripId, userId);
    }

    // New method: Validate multiple users are participants (for shared expenses)
    @Transactional(readOnly = true)
    public void validateUsersAreParticipants(Integer tripId, List<Long> userIds) {
        for (Long userId : userIds) {
            if (!repository.existsByTripIdAndUserId(tripId, userId)) {
                throw new IllegalArgumentException("User " + userId + " is not a participant of trip " + tripId);
            }
        }
    }

    public void removeParticipant(Integer id) {
        dal.deleteParticipant(id);
    }
}
