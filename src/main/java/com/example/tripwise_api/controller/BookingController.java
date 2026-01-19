package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.BookingMapper;
import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.dto.response.BookingResponse;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.BookingService;
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
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // POST /api/trips/{tripId}/bookings - save booking for a specific trip
    @PostMapping("/trips/{tripId}/bookings")
    public ResponseEntity<?> createBooking(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateBookingRequest request) {
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
            Booking booking = BookingMapper.toEntity(request);
            Booking saved = bookingService.createBookingForUser(user, booking, tripId);
            BookingResponse resp = BookingMapper.toResponse(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save booking");
        }
    }

    // GET /api/trips/{tripId}/bookings - get all bookings for a specific trip
    @GetMapping("/trips/{tripId}/bookings")
    public ResponseEntity<?> getBookingsByTrip(@PathVariable Long tripId) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        List<Booking> bookings = bookingService.getBookingsByTripId(tripId, user.getId());
        return ResponseEntity.ok(BookingMapper.toResponseList(bookings));
    }

    // GET /api/bookings/{id} - get booking by id for current user
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Booking booking = bookingOpt.get();
        if (!booking.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Access denied");
        }
        return ResponseEntity.ok(BookingMapper.toResponse(booking));
    }

    // DELETE /api/bookings/{id} - delete booking by id for current user
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Booking booking = bookingOpt.get();
        if (!booking.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Access denied");
        }
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete booking");
        }
    }

    // PUT /api/bookings/{id} - update booking by id for current user
    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookingRequest request) {
        Optional<String> usernameOpt = ControllerUtils.getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) {
            return ControllerUtils.unauthorized();
        }
        Optional<User> userOpt = userService.findByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Booking booking = bookingOpt.get();
        if (!booking.getUser().getId().equals(user.getId())) {
            return ControllerUtils.forbidden("Access denied");
        }
        try {
            Booking updated = bookingService.updateBooking(id, request);
            BookingResponse resp = BookingMapper.toResponse(updated);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update booking");
        }
    }
}
