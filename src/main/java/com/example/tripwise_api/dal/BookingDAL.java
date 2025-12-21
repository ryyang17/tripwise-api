package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookingDAL {
    private final BookingRepository bookingRepository;

    public BookingDAL(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    public List<Booking> getBookingsByTripIdAndUserId(Long tripId, Long userId) {
        return bookingRepository.findByTripIdAndUser_Id(tripId, userId);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
