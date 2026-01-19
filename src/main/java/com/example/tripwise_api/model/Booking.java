package com.example.tripwise_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking")
public class Booking extends BaseTripwiseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", length = 20)
    private BookingType bookingType;

    @Column(name = "booking_reference", length = 200)
    private String bookingReference;

    @Column(name = "price", precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "currency", length = 10)
    private String currency;

    // Store search params or details as JSON string from Amadeus
    @Column(name = "search_params", columnDefinition = "TEXT")
    private String searchParams;

    public Booking() {}

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
