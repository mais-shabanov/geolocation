package com.example.geolocation.service;

import com.example.geolocation.dto.request.BuildingRequest;
import com.example.geolocation.dto.response.BuildingResponse;
import com.example.geolocation.dto.response.BuildingsResponse;
import com.example.geolocation.dto.response.PointProperties;
import com.example.geolocation.entity.Building;
import com.example.geolocation.exception.BuildingNotFoundException;
import com.example.geolocation.repository.BuildingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional
    public Long createBuilding(BuildingRequest buildingRequest) {

        List<List<List<Double>>> coordinates = buildingRequest.geometry().coordinates();
        Coordinate[] polygonCoordinates = convertCoordinates(coordinates);

        Building building = new Building();

        GeometryFactory geometryFactory = new GeometryFactory();

        LinearRing linearRing = geometryFactory.createLinearRing(polygonCoordinates);

        Polygon polygon = geometryFactory.createPolygon(linearRing);

        building.setPolygon(polygon);

        BuildingRequest.Properties properties = buildingRequest.properties();
        Integer index = properties.index();
        building.setIndex(index);

        Building savedBuilding = buildingRepository.save(building);

        return savedBuilding.getId();
    }

    @Transactional
    public void updateBuilding(Long buildingId, BuildingRequest buildingRequest) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Building not found"));

        List<List<List<Double>>> coordinates = buildingRequest.geometry().coordinates();
        Coordinate[] polygonCoordinates = convertCoordinates(coordinates);

        GeometryFactory geometryFactory = new GeometryFactory();

        LinearRing linearRing = geometryFactory.createLinearRing(polygonCoordinates);

        Polygon polygon = geometryFactory.createPolygon(linearRing);

        building.setPolygon(polygon);

        BuildingRequest.Properties properties = buildingRequest.properties();
        Integer index = properties.index();
        building.setIndex(index);

        buildingRepository.save(building);
    }


    public BuildingsResponse getAllBuildings() {
        return new BuildingsResponse("FeatureCollection", buildingRepository.findAll().stream().map(BuildingResponse::convertToResponse).toList());
    }

    public BuildingResponse getBuilding(Long buildingId) {
        return BuildingResponse.convertToResponse(buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("Building not found by id : " + buildingId)));
    }

    public void deleteBuilding(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new BuildingNotFoundException("Building not found by id : " + buildingId);
        }
        buildingRepository.deleteById(buildingId);
    }

    public List<PointProperties> getIntersectingPoints(@PathVariable Long buildingId) {
        List<Map<String, Object>> pointPropertiesList = buildingRepository.findIntersectingPointProperties(buildingId);
        List<PointProperties> intersectingPoints = new ArrayList<>();

        for (Map<String, Object> pointPropertiesMap : pointPropertiesList) {
            PointProperties pointProperties = new PointProperties();
            pointProperties.setName((String) pointPropertiesMap.get("name"));
            pointProperties.setIndex((Integer) pointPropertiesMap.get("index"));
            pointProperties.setPhone((String) pointPropertiesMap.get("phone"));
            pointProperties.setAmenity((String) pointPropertiesMap.get("amenity"));
            pointProperties.setGeotype((String) pointPropertiesMap.get("geotype"));
            pointProperties.setWebsite((String) pointPropertiesMap.get("website"));
            pointProperties.setInternet_access((String) pointPropertiesMap.get("internet_access"));
            intersectingPoints.add(pointProperties);
        }

        return intersectingPoints;
    }

    private Coordinate[] convertCoordinates(List<List<List<Double>>> coordinates) {
        List<Coordinate> coordinateList = new ArrayList<>();

        for (List<List<Double>> polygon : coordinates) {
            for (List<Double> coordinate : polygon) {
                double longitude = coordinate.get(0);
                double latitude = coordinate.get(1);
                coordinateList.add(new Coordinate(longitude, latitude));
            }
        }

        return coordinateList.toArray(new Coordinate[0]);
    }
}
