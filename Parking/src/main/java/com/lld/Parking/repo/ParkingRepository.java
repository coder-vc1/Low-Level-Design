package com.lld.Parking.repo;

import com.lld.Parking.entity.ParkingSpot;
import com.lld.Parking.entity.ParkingTicket;
import com.lld.Parking.entity.VehicleType;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public interface ParkingRepository {

  ParkingSpot findAvailableSpot(VehicleType vehicleType);

  void saveTicket(ParkingTicket ticket);

  ParkingTicket findTicketById(String ticketId);

  void updateSpot(ParkingSpot spot);

  List<ParkingSpot> getAllSpots();
}