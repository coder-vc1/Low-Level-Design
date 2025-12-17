package com.lld.Parking.service;


import com.lld.Parking.entity.ParkingSpot;
import com.lld.Parking.entity.ParkingTicket;
import com.lld.Parking.entity.Vehicle;
import com.lld.Parking.repo.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingService {

  private final ParkingRepository repository;

  // Synchronized to handle concurrency for 1hr interview simplicity
  // Optimally: Use DB locking or optimistic locking
  public synchronized ParkingTicket entry(Vehicle vehicle) {
    ParkingSpot spot = repository.findAvailableSpot(vehicle.getType());
    if (spot == null) {
      throw new RuntimeException("Parking Full for type: " + vehicle.getType());
    }

    // Occupy Spot
    spot.setOccupied(true);
    spot.setVehicleLicensePlate(vehicle.getLicensePlate());
    repository.updateSpot(spot);

    // Generate Ticket
    ParkingTicket ticket = new ParkingTicket(
        UUID.randomUUID().toString(),
        spot.getId(),
        vehicle.getLicensePlate(),
        LocalDateTime.now(),
        0.0,
        false
    );
    repository.saveTicket(ticket);
    System.out.println("Ticket Generated: " + ticket.getTicketId());
    return ticket;
  }

  public synchronized ParkingTicket exit(String ticketId) {
    ParkingTicket ticket = repository.findTicketById(ticketId);
    if (ticket == null || ticket.isPaid()) {
      throw new RuntimeException("Invalid or already paid ticket");
    }

    // Release Spot
    ParkingSpot spot = repository.getAllSpots().stream()
        .filter(s -> s.getId().equals(ticket.getSpotId()))
        .findFirst().orElseThrow();

    spot.setOccupied(false);
    spot.setVehicleLicensePlate(null);
    repository.updateSpot(spot);

    // Calculate Fee (Simple Strategy: $10 per hour)
    long hours = Duration.between(ticket.getEntryTime(), LocalDateTime.now()).toHours();
    if (hours == 0) hours = 1; // Minimum 1 hour
    ticket.setFee(hours * 10.0);
    ticket.setPaid(true);

    repository.saveTicket(ticket);
    System.out.println("Vehicle exited. Fee: " + ticket.getFee());
    return ticket;
  }
}