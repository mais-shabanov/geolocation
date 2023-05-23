package com.example.geolocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class GeolocationApplication implements CommandLineRunner {

	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}

	private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/location";
	private static final String DATABASE_USERNAME = "postgres";
	private static final String DATABASE_PASSWORD = "123456789";
	String tableName = "";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(GeolocationApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		// Driver
		DriverManagerDataSource dataSource = new DriverManagerDataSource(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
		jdbcTemplate = new JdbcTemplate(dataSource);


		// Initial Data Loading
		String createPoints = "create table points(\n" +
				"    id serial primary key,\n" +
				"    geometry Geometry(Point, 4326),\n" +
				"    properties jsonb\n" +
				");";
		jdbcTemplate.execute(createPoints);
		String createRoads = "create table roads(\n" +
				"    id serial primary key,\n" +
				"    geometry Geometry(Geometry, 4326)\n" +
				");\n";
		jdbcTemplate.execute(createRoads);


		//Points

		tableName = "points";
		String pointsFilePath = "src/main/java/com/example/geolocation/data/poi.geojson";
		String pointsJson = null;
		try {
			pointsJson = new String(Files.readAllBytes(Paths.get(pointsFilePath)));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(pointsJson);
		JsonNode features = jsonNode.get("features");
		for (var feature : features) {
			JsonNode propertiesNode = feature.get("properties");
			JsonNode geometryNode = feature.get("geometry");
			String properties = objectMapper.writeValueAsString(propertiesNode);
			String geometry = objectMapper.writeValueAsString(geometryNode);
			String insertQuery = "INSERT INTO " + tableName + "(geometry, properties) VALUES (ST_GeomFromGeoJSON(?), CAST(? AS JSONB))";
			jdbcTemplate.update(insertQuery, geometry, properties);
		}

		//Building

		tableName = "buildings";
		String firstBuildingPath = "src/main/java/com/example/geolocation/data/bina1.geojson";
		String secondBuildingPath = "src/main/java/com/example/geolocation/data/bina2.geojson";

		try {
			processGeoJSONFile(firstBuildingPath);
			processGeoJSONFile(secondBuildingPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Roads

		tableName = "roads";

		String roadsFilePath = "src/main/java/com/example/geolocation/data/yollar.geojson";
		String roadsJson = null;
		try {
			roadsJson = new String(Files.readAllBytes(Paths.get(roadsFilePath)));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		GeoJsonReader roadReader = new GeoJsonReader();
		try {
			Geometry yolGeometry = roadReader.read(roadsJson);
			if (yolGeometry != null && yolGeometry instanceof GeometryCollection) {
				GeometryCollection yolGeometryCollection = (GeometryCollection) yolGeometry;
				List<LineString> lineStrings = new ArrayList<>();
				for (int i = 0; i < yolGeometryCollection.getNumGeometries(); i++) {
					Geometry geometry = yolGeometryCollection.getGeometryN(i);
					if (geometry instanceof LineString) {
						LineString lineString = (LineString) geometry;
						boolean intersects = false;
						for (LineString existingLineString : lineStrings) {
							if (lineString.intersects(existingLineString)) {
								intersects = true;
								break;
							}
						}
						if (!intersects) {
							lineStrings.add(lineString);
						}
					}
				}
				if (!lineStrings.isEmpty()) {
					String insertQuery = "INSERT INTO " + tableName + "(geometry) VALUES (ST_SetSRID(ST_GeomFromText(?, 4326), 4326))";
					for (LineString lineString : lineStrings) {
						jdbcTemplate.update(insertQuery, lineString.toText());
					}
				} else {
					System.out.println("No non-intersecting road geometries found.");
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void processGeoJSONFile(String filePath) throws IOException {

		String buildingsJson = new String(Files.readAllBytes(Paths.get(filePath)));
		tableName = "buildings";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(buildingsJson);
		JsonNode features = jsonNode.get("features");
		for (var feature : features) {
			JsonNode geometryNode = feature.get("geometry");
			JsonNode propertiesNode = feature.get("properties");
			JsonNode indexNode = propertiesNode.get("index");
			String geometry = objectMapper.writeValueAsString(geometryNode);
			String index = objectMapper.writeValueAsString(indexNode);
			String insertQuery = "INSERT INTO " + tableName + "(index, polygon) SELECT ?, ST_GeomFromGeoJSON(?) WHERE NOT EXISTS (SELECT 1 FROM " + tableName + " WHERE polygon = ST_GeomFromGeoJSON(?))";
			jdbcTemplate.update(insertQuery, Integer.valueOf(index), geometry, geometry);
		}

	}

}
