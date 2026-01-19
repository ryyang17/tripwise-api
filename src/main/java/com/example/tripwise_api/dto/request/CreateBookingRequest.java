package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateBookingRequest extends BaseRequest {
    @NotNull
    private String bookingType; // FLIGHT or HOTEL
    private String bookingReference;
    private BigDecimal price;
    private String currency;
    private String searchParams;

    public CreateBookingRequest() {}

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
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
