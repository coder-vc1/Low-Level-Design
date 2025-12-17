package com.lld.Parking;

import com.lld.Parking.entity.ParkingTicket;
import com.lld.Parking.entity.Vehicle;
import com.lld.Parking.entity.VehicleType;
import com.lld.Parking.service.ParkingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ParkingApplication {

  public static void main(String[] args) {
    // 1. Initialize Context
    ConfigurableApplicationContext context = SpringApplication.run(ParkingApplication.class, args);

    // 2. Fetch Service manually
    ParkingService service = context.getBean(ParkingService.class);

    System.out.println("--- STARTING SIMULATION ---");

    try {
      // 3. Demo Logic

      // Case 1: Bike Entry
      System.out.println("1. Bike Entering...");
      Vehicle bike = new Vehicle("KA-01-1234", VehicleType.BIKE);
      ParkingTicket bikeTicket = service.entry(bike);
      System.out.println("   Allocated Spot: " + bikeTicket.getSpotId());

      // Case 2: Car Entry
      System.out.println("2. Car Entering...");
      Vehicle car = new Vehicle("KA-05-9999", VehicleType.CAR);
      ParkingTicket carTicket = service.entry(car);
      System.out.println("   Allocated Spot: " + carTicket.getSpotId());

      // Case 3: Bike Exit
      System.out.println("3. Bike Exiting...");
      // Simulate time delay if needed, or rely on min 1 hr logic
      ParkingTicket receipt = service.exit(bikeTicket.getTicketId());
      System.out.println("   Exit Complete. Fee Paid: $" + receipt.getFee());

    } catch (Exception e) {
      System.err.println("Simulation Error: " + e.getMessage());
    }

    System.out.println("--- SIMULATION END ---");
  }

}
