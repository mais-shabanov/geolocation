package com.example.geolocation.dto.response;

import com.example.geolocation.entity.Building;
import org.locationtech.jts.geom.Polygon;

public record BuildingResponse(
        String type,
        Polygon geometry,
        GeoJsonProperties properties,
        Long id
        ) {
    public static BuildingResponse convertToResponse(Building building) {
        Long id = building.getId();
        Integer index = building.getIndex();
        Polygon polygon = building.getPolygon();
        GeoJsonProperties properties = new GeoJsonProperties("yes", "Polygon", index);

        return new BuildingResponse("Feature", polygon, properties, id);
    }

    public record GeoJsonProperties(
            String building,
            String geotype,
            Integer index
    ) {}
}


