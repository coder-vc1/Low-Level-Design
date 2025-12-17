package com.lld.Parking.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkingTicket {
  private String ticketId;
  private String spotId;
  private String licensePlate;
  private LocalDateTime entryTime;
  private double fee; // Null initially
  private boolean isPaid;
}
