package com.example.geolocation;

import com.example.geolocation.dto.request.BuildingRequest;
import com.example.geolocation.dto.response.BuildingResponse;
import com.example.geolocation.dto.response.BuildingsResponse;
import com.example.geolocation.dto.response.PointProperties;
import com.example.geolocation.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping
    public ResponseEntity<Long> createBuilding(@RequestBody BuildingRequest buildingRequest) {
        return ResponseEntity.ok(buildingService.createBuilding(buildingRequest));
    }

    @PutMapping("/{buildingId}")
    public ResponseEntity<Void> updateBuilding(@PathVariable("buildingId") Long buildingId, @RequestBody BuildingRequest buildingRequest) {
        buildingService.updateBuilding(buildingId, buildingRequest);
        return new ResponseEntity<>(OK);
    }

    @GetMapping
    public ResponseEntity<BuildingsResponse> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }


    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingResponse> getBuilding(@PathVariable("buildingId") Long buildingId) {
        return ResponseEntity.ok(buildingService.getBuilding(buildingId));
    }

    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable("buildingId") Long buildingId) {
        buildingService.deleteBuilding(buildingId);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/getPoi/{buildingId}")
    public ResponseEntity<List<PointProperties>> getIntersectingPoints(@PathVariable("buildingId") Long buildingId) {
        return ResponseEntity.ok(buildingService.getIntersectingPoints(buildingId));
    }

}
