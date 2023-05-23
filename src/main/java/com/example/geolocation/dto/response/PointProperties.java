package com.example.geolocation.dto.response;


public class PointProperties {
    private String name;
    private Integer index;
    private String phone;
    private String amenity;
    private String geotype;
    private String website;
    private String internet_access;

    public PointProperties() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getGeotype() {
        return geotype;
    }

    public void setGeotype(String geotype) {
        this.geotype = geotype;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getInternet_access() {
        return internet_access;
    }

    public void setInternet_access(String internet_access) {
        this.internet_access = internet_access;
    }

    // Add constructor, getters, and setters
    // ...
}

