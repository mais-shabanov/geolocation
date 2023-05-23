package com.example.geolocation.dto.request;

import java.util.List;

public record BuildingRequest(String type, Geometry geometry, Properties properties) {

    public record Geometry(String type, List<List<List<Double>>> coordinates) {
    }

    public record Properties(int index, String geotype) {
        // Additional properties go here
    }
}





