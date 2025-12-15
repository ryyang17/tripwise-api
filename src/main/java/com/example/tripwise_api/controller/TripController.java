package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.TripMapper;
import com.example.tripwise_api.dto.mapper.TripParticipantMapper;
import com.example.tripwise_api.dto.request.CreateParticipantRequest;
import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateParticipantRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.dto.response.TripParticipantResponse;
import com.example.tripwise_api.dto.response.TripResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.service.TripService;
import com.example.tripwise_api.service.TripParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(originPatterns = "*")
public class TripController {

    private final TripService tripService;
    private final TripParticipantService tripParticipantService;
    private final TripParticipantMapper tripParticipantMapper;
    private final TripMapper tripMapper;

    public TripController(TripService tripService, TripParticipantService tripParticipantService,
                          TripParticipantMapper tripParticipantMapper, TripMapper tripMapper) {
        this.tripService = tripService;
        this.tripParticipantService = tripParticipantService;
        this.tripParticipantMapper = tripParticipantMapper;
        this.tripMapper = tripMapper;
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(tripMapper.toResponseList(trips));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<TripResponse>> getTripsByUserId(@RequestParam Long userId) {
        List<Trip> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(tripMapper.toResponseList(trips));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .map(trip -> ResponseEntity.ok(tripMapper.toResponse(trip)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        Trip trip = TripMapper.toEntity(request);
        Trip savedTrip = tripService.createTrip(trip);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tripMapper.toResponse(savedTrip));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTripRequest request) {
        return tripService.getTripById(id)
                .map(existingTrip -> {
                    TripMapper.updateEntity(existingTrip, request);
                    Trip updatedTrip = tripService.updateTrip(id, existingTrip);
                    return ResponseEntity.ok(tripMapper.toResponse(updatedTrip));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<TripParticipantResponse>> getParticipants(@PathVariable Integer id) {
        List<TripParticipant> participants = tripParticipantService.getParticipantsByTripIdWithUserDetails(id);
        return ResponseEntity.ok(tripParticipantMapper.toResponseList(participants));
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<TripParticipantResponse> addParticipant(
            @PathVariable Integer id,
            @Valid @RequestBody CreateParticipantRequest request) {
        TripParticipant participant = tripParticipantMapper.toEntity(request, id);
        TripParticipant savedParticipant = tripParticipantService.addParticipant(participant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tripParticipantMapper.toResponse(savedParticipant));
    }

    @PutMapping("/participants/{participantId}")
    public ResponseEntity<TripParticipantResponse> updateParticipant(
            @PathVariable Integer participantId,
            @Valid @RequestBody UpdateParticipantRequest request) {
        try {
            TripParticipant updatedParticipant = tripParticipantService.updateParticipant(
                    participantId,
                    request.getRole(),
                    request.getIsOwner()
            );
            return ResponseEntity.ok(tripParticipantMapper.toResponse(updatedParticipant));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable Integer participantId) {
        tripParticipantService.removeParticipant(participantId);
        return ResponseEntity.noContent().build();
    }
}