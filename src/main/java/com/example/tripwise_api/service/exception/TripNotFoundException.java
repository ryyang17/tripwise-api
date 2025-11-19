package com.example.tripwise_api.service.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
