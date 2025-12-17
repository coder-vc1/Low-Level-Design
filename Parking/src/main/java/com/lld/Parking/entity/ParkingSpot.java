package com.lld.Parking.entity;

import lombok.Data;

@Data
public class ParkingSpot {
  private String id;
  private SpotType type;
  private boolean isOccupied;
  private String vehicleLicensePlate;

  public ParkingSpot(String id, SpotType type) {
    this.id = id;
    this.type = type;
    this.isOccupied = false;
  }
}