package com.lld.Parking.dto;


import com.lld.Parking.entity.VehicleType;
import lombok.Data;

@Data
public class EntryRequest {
  private String licensePlate;
  private VehicleType type;
}
