package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.MapMapper;
import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.dto.response.MapResponse;
import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.MapService;
import com.example.tripwise_api.service.UserService;
import com.example.tripwise_api.util.ControllerUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class MapController {

    private final MapService mapService;
    private final UserService userService;

    public MapController(MapService mapService, UserService userService) {
        this.mapService = mapService;
        this.userService = userService;
    }

    // POST /api/trips/{tripId}/maps - save map for a specific trip
    @PostMapping("/trips/{tripId}/maps")
    public ResponseEntity<?> createMap(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateMapRequest request) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        if (request.getRawPayload() == null || request.getRawPayload().isBlank()) {
            return ResponseEntity.badRequest().body("rawPayload is required");
        }
        try {
            MapData map = MapMapper.toEntity(request);
            MapData saved = mapService.createMapForUser(user, map, tripId);
            MapResponse resp = MapMapper.toResponse(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save map");
        }
    }

    // GET /api/trips/{tripId}/maps - get all maps for a specific trip
    @GetMapping("/trips/{tripId}/maps")
    public ResponseEntity<?> getMapsByTrip(@PathVariable Long tripId) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        List<MapData> maps = mapService.getMapsByTripId(tripId, user.getId());
        return ResponseEntity.ok(MapMapper.toResponseList(maps));
    }

    // GET /api/maps/{id} - get map by id for current user
    @GetMapping("/maps/{id}")
    public ResponseEntity<?> getMapById(@PathVariable Long id) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<MapData> mapOpt = mapService.getMapById(id);
        if (mapOpt.isEmpty()) {
            return ControllerUtils.notFound("Map");
        }
        MapData map = mapOpt.get();
        if (!map.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Not authorized to access this map");
        }
        return ResponseEntity.ok(MapMapper.toResponse(map));
    }

    // GET /api/maps - get all maps for current user
    @GetMapping("/maps")
    public ResponseEntity<?> getAllMapsForUser() {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        List<MapData> maps = mapService.getMapsForUser(user.getId());
        return ResponseEntity.ok(MapMapper.toResponseList(maps));
    }

    // PUT /api/maps/{id} - update map by id
    @PutMapping("/maps/{id}")
    public ResponseEntity<?> updateMap(
            @PathVariable Long id,
            @Valid @RequestBody CreateMapRequest request) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<MapData> mapOpt = mapService.getMapById(id);
        if (mapOpt.isEmpty()) {
            return ControllerUtils.notFound("Map");
        }
        MapData map = mapOpt.get();
        if (!map.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Not authorized to update this map");
        }
        try {
            MapData updated = mapService.updateMap(id, request);
            return ResponseEntity.ok(MapMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update map");
        }
    }

    // DELETE /api/maps/{id} - delete map by id
    @DeleteMapping("/maps/{id}")
    public ResponseEntity<?> deleteMap(@PathVariable Long id) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<MapData> mapOpt = mapService.getMapById(id);
        if (mapOpt.isEmpty()) {
            return ControllerUtils.notFound("Map");
        }
        MapData map = mapOpt.get();
        if (!map.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Not authorized to delete this map");
        }
        try {
            mapService.deleteMap(id);
            return ResponseEntity.ok("Map deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete map");
        }
    }
}

