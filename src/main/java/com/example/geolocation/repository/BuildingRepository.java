package com.example.geolocation.repository;

import com.example.geolocation.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Query(value = "SELECT p.properties FROM points p WHERE ST_Intersects(p.geometry, (SELECT polygon FROM buildings WHERE id = :buildingId))", nativeQuery = true)
    List<Map<String, Object>> findIntersectingPointProperties(@Param("buildingId") Long buildingId);


}

