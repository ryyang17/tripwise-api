package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.repository.TripParticipantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TripParticipantDAL {
    private final TripParticipantRepository repository;

    public TripParticipantDAL(TripParticipantRepository repository) {
        this.repository = repository;
    }

    public TripParticipant saveParticipant(TripParticipant participant) {
        return repository.save(participant);
    }

    public List<TripParticipant> getParticipantsByTripId(Integer tripId) {
        return repository.findByTripId(tripId);
    }

    public Optional<TripParticipant> getParticipantById(Integer id) {
        return repository.findById(id);
    }

    public void deleteParticipant(Integer id) {
        repository.deleteById(id);
    }

    public boolean existsById(Integer id) { return repository.existsById(id); }
}
