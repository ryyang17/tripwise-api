package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.BookingDAL;
import com.example.tripwise_api.dto.mapper.BookingMapper;
import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    private final BookingDAL bookingDAL;

    public BookingService(BookingDAL bookingDAL) {
        this.bookingDAL = bookingDAL;
    }

    public Booking createBookingForUser(User user, Booking booking, Long tripId) {
        booking.setUser(user);
        booking.setTripId(tripId);
        return bookingDAL.saveBooking(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingDAL.getBookingsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTripId(Long tripId, Long userId) {
        return bookingDAL.getBookingsByTripIdAndUserId(tripId, userId);
    }

    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        return bookingDAL.getBookingById(id);
    }

    public void deleteBooking(Long id) {
        bookingDAL.deleteBooking(id);
    }

    public Booking updateBooking(Long id, CreateBookingRequest request) {
        Booking existingBooking = bookingDAL.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));

        // Update fields from request
        Booking updatedData = BookingMapper.toEntity(request);
        existingBooking.setBookingType(updatedData.getBookingType());
        existingBooking.setProvider(updatedData.getProvider());
        existingBooking.setBookingReference(updatedData.getBookingReference());
        existingBooking.setPrice(updatedData.getPrice());
        existingBooking.setCurrency(updatedData.getCurrency());
        existingBooking.setSearchParams(updatedData.getSearchParams());
        existingBooking.setRawPayload(updatedData.getRawPayload());

        return bookingDAL.saveBooking(existingBooking);
    }
}
