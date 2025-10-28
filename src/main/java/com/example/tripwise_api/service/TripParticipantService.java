package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripParticipantDAL;
import com.example.tripwise_api.model.TripParticipant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripParticipantService {
    private final TripParticipantDAL dal;

    public TripParticipantService(TripParticipantDAL dal) {
        this.dal = dal;
    }

    public TripParticipant addParticipant(TripParticipant participant) {
        return dal.saveParticipant(participant);
    }

    public List<TripParticipant> getParticipantsByTripId(Integer tripId) {
        return dal.getParticipantsByTripId(tripId);
    }

    public Optional<TripParticipant> getParticipantById(Integer id) {
        return dal.getParticipantById(id);
    }

    public void removeParticipant(Integer id) {
        dal.deleteParticipant(id);
    }
}
