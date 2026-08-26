package com.simon.scheduledawg.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "userSettings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String homeAddress;

    private Double homeLatitude;

    private Double homeLongitude;

    public UserSettings() {

    }

    public UserSettings(String homeAddress, Double homeLatitude, Double homeLongitude) {
        this.homeAddress = homeAddress;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getHomeAddress() {
        return homeAddress;
    }
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
    public Double getHomeLatitude() {
        return homeLatitude;
    }
    public void setHomeLatitude(Double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }
    public Double getHomeLongitude() {
        return homeLongitude;
    }
    public void setHomeLongitude(Double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }


}
