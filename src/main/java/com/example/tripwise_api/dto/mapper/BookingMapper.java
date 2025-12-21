package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.dto.response.BookingResponse;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.BookingType;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toEntity(CreateBookingRequest req) {
        Booking b = new Booking();
        if (req.getBookingType() != null) {
            try {
                b.setBookingType(BookingType.valueOf(req.getBookingType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                b.setBookingType(BookingType.OTHER);
            }
        } else {
            b.setBookingType(BookingType.OTHER);
        }

        b.setProvider(req.getProvider());
        b.setBookingReference(req.getBookingReference());
        b.setPrice(req.getPrice());
        b.setCurrency(req.getCurrency());
        b.setSearchParams(req.getSearchParams());
        b.setRawPayload(req.getRawPayload());
        return b;
    }

    public static BookingResponse toResponse(Booking b) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setTripId(b.getTripId());
        r.setBookingType(b.getBookingType() != null ? b.getBookingType().name() : null);
        r.setProvider(b.getProvider());
        r.setBookingReference(b.getBookingReference());
        r.setPrice(b.getPrice());
        r.setCurrency(b.getCurrency());
        r.setSearchParams(b.getSearchParams());
        r.setRawPayload(b.getRawPayload());
        r.setCreatedDate(b.getCreatedDate());
        return r;
    }

    public static List<BookingResponse> toResponseList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toResponse).collect(Collectors.toList());
    }
}
