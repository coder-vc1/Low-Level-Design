package com.lld.Parking.repo;

import com.lld.Parking.entity.ParkingSpot;
import com.lld.Parking.entity.ParkingTicket;
import com.lld.Parking.entity.SpotType;
import com.lld.Parking.entity.VehicleType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;


@Repository
public class InMemoryParkingRepository implements ParkingRepository {

  // Concurrent Maps for Thread Safety
  private final Map<String, ParkingSpot> spots = new ConcurrentHashMap<>();
  private final Map<String, ParkingTicket> tickets = new ConcurrentHashMap<>();

  public InMemoryParkingRepository() {
    // Initialize Dummy Data: 10 Spots
    // 5 Bike spots, 5 Car spots
    for (int i = 1; i <= 5; i++) {
      spots.put("S-B-" + i, new ParkingSpot("S-B-" + i, SpotType.BIKE_SPOT));
      spots.put("S-C-" + i, new ParkingSpot("S-C-" + i, SpotType.CAR_SPOT));
    }
  }

  @Override
  public ParkingSpot findAvailableSpot(VehicleType vehicleType) {
    SpotType requiredSpotType = mapVehicleToSpot(vehicleType);

    // Simple linear search (In DB this would be a SELECT query)
    return spots.values().stream()
        .filter(s -> !s.isOccupied() && s.getType() == requiredSpotType)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void saveTicket(ParkingTicket ticket) {
    tickets.put(ticket.getTicketId(), ticket);
  }

  @Override
  public ParkingTicket findTicketById(String ticketId) {
    return tickets.get(ticketId);
  }

  @Override
  public void updateSpot(ParkingSpot spot) {
    spots.put(spot.getId(), spot);
  }

  @Override
  public List<ParkingSpot> getAllSpots() {
    return new ArrayList<>(spots.values());
  }

  private SpotType mapVehicleToSpot(VehicleType vt) {
    if (vt == VehicleType.BIKE) {
      return SpotType.BIKE_SPOT;
    }
    if (vt == VehicleType.CAR) {
      return SpotType.CAR_SPOT;
    }
    return SpotType.LARGE_V_SPOT;
  }
}
