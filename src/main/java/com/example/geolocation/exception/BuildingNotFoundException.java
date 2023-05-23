package com.example.geolocation.exception;

public class BuildingNotFoundException extends RuntimeException{
    public BuildingNotFoundException(String message) {
        super(message);
    }
}
