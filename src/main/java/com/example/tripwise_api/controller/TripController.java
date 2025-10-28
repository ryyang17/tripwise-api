package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.TripMapper;
import com.example.tripwise_api.dto.mapper.TripParticipantMapper;
import com.example.tripwise_api.dto.request.CreateParticipantRequest;
import com.example.tripwise_api.dto.request.CreateTripRequest;
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
@CrossOrigin(origins = "*")
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

    // GET /api/trips - Alle trips
    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        // Gebruik mapper om lijst van entities naar lijst van DTOs te converteren
        return ResponseEntity.ok(tripMapper.toResponseList(trips));
    }

    // GET /api/trips?userId={userId} - Trips van een gebruiker (frontend verwacht dit)
    @GetMapping(params = "userId")
    public ResponseEntity<List<TripResponse>> getTripsByUserId(@RequestParam Long userId) {
        List<Trip> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(tripMapper.toResponseList(trips));
    }

    // GET /api/trips/{id} - Één trip ophalen
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .map(trip -> ResponseEntity.ok(tripMapper.toResponse(trip)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/trips - Nieuwe trip aanmaken
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        // Converteer request DTO naar entity
        Trip trip = TripMapper.toEntity(request);

        // Sla op via service
        Trip savedTrip = tripService.createTrip(trip);

        // Converteer terug naar response DTO (met berekend isActive veld!)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tripMapper.toResponse(savedTrip));
    }

    // PUT /api/trips/{id} - Trip updaten
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTripRequest request) {

        return tripService.getTripById(id)
                .map(existingTrip -> {
                    // Update entity met request data
                    TripMapper.updateEntity(existingTrip, request);

                    // Sla updated trip op
                    Trip updatedTrip = tripService.updateTrip(id, existingTrip);

                    // Return response DTO (met berekend isActive veld!)
                    return ResponseEntity.ok(tripMapper.toResponse(updatedTrip));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/trips/{id} - Trip verwijderen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }


    // GET /api/trips/{id}/participants - list participants of a trip
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<TripParticipantResponse>> getParticipants(@PathVariable Integer id) {
        List<TripParticipant> participants = tripParticipantService.getParticipantsByTripId(id);
        // STAP 3: Mapper vult automatisch BEIDE id en participantId velden!
        return ResponseEntity.ok(tripParticipantMapper.toResponseList(participants));
    }

    // POST /api/trips/{id}/participants - add participant to the trip
    @PostMapping("/{id}/participants")
    public ResponseEntity<TripParticipantResponse> addParticipant(
            @PathVariable Integer id,
            @Valid @RequestBody CreateParticipantRequest request) {

        // Converteer request naar entity
        TripParticipant participant = TripParticipantMapper.toEntity(request, id);

        // Sla op
        TripParticipant savedParticipant = tripParticipantService.addParticipant(participant);

        // Return response DTO (met beide ID velden!)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tripParticipantMapper.toResponse(savedParticipant));
    }

    // DELETE /api/trips/participants/{participantId} - remove participant
    @DeleteMapping("/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable Integer participantId) {
        tripParticipantService.removeParticipant(participantId);
        return ResponseEntity.noContent().build();
    }
}