package com.lld.Parking.controller;

import com.lld.Parking.dto.EntryRequest;
import com.lld.Parking.dto.ExitRequest;
import com.lld.Parking.entity.ParkingTicket;
import com.lld.Parking.entity.Vehicle;
import com.lld.Parking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

  private final ParkingService service;

  @PostMapping("/entry")
  public ResponseEntity<ParkingTicket> entry(@RequestBody EntryRequest request) {
    Vehicle vehicle = new Vehicle(request.getLicensePlate(), request.getType());
    return ResponseEntity.ok(service.entry(vehicle));
  }

  @PostMapping("/exit")
  public ResponseEntity<ParkingTicket> exit(@RequestBody ExitRequest request) {
    return ResponseEntity.ok(service.exit(request.getTicketId()));
  }
}