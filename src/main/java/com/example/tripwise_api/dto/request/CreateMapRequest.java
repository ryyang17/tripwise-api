package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateMapRequest {

    @NotBlank
    private String provider;

    // Waypoints as JSON string (array of coordinates)
    private String waypoints;

    private String distance;

    private String duration;

    private String location;

    // Full raw payload from map API as JSON string
    @NotBlank
    private String rawPayload;

    public CreateMapRequest() {
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }
}

