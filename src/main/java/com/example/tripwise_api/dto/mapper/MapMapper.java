package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.dto.response.MapResponse;
import com.example.tripwise_api.model.MapData;

import java.util.List;
import java.util.stream.Collectors;

public class MapMapper {

    public static MapData toEntity(CreateMapRequest req) {
        MapData m = new MapData();
        m.setProvider(req.getProvider());
        m.setWaypoints(req.getWaypoints());
        m.setDistance(req.getDistance());
        m.setDuration(req.getDuration());
        m.setLocation(req.getLocation());
        m.setRawPayload(req.getRawPayload());
        return m;
    }

    public static MapResponse toResponse(MapData m) {
        MapResponse r = new MapResponse();
        r.setId(m.getId());
        r.setTripId(m.getTripId());
        r.setProvider(m.getProvider());
        r.setWaypoints(m.getWaypoints());
        r.setDistance(m.getDistance());
        r.setDuration(m.getDuration());
        r.setLocation(m.getLocation());
        r.setRawPayload(m.getRawPayload());
        r.setCreatedDate(m.getCreatedDate());
        return r;
    }

    public static List<MapResponse> toResponseList(List<MapData> maps) {
        return maps.stream().map(MapMapper::toResponse).collect(Collectors.toList());
    }
}

