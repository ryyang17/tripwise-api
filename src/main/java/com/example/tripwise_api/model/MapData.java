package com.example.tripwise_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "map")
public class MapData extends BaseTripwiseEntity {

    // Waypoints stored as JSON string
    @Column(name = "waypoints", columnDefinition = "TEXT")
    private String waypoints;

    @Column(name = "distance", length = 50)
    private String distance;

    @Column(name = "duration", length = 50)
    private String duration;

    @Column(name = "location")
    private String location;

    public MapData() {}

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
}
