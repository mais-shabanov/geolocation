package com.example.geolocation.dto.response;

import java.util.List;

public record BuildingsResponse(String type, List<BuildingResponse> features) {
}
