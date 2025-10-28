package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExchangeRateResponse {
    private String from;
    private String to;
    private BigDecimal rate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    // Constructors
    public ExchangeRateResponse() {}

    public ExchangeRateResponse(String from, String to, BigDecimal rate, LocalDateTime lastUpdated) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}

