package com.lld.Parking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vehicle {
  private String licensePlate;
  private VehicleType type;
}
