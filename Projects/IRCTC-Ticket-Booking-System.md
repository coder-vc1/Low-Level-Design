---
#Low-Level Design (LLD) for IRCTC Ticket Booking System.
---

### 1) Functional and Non-functional Requirements

| Type | Requirement | Description |
| --- | --- | --- |
| **Functional** | **Search Trains** | Find trains between Source and Destination. |
|  | **Book Ticket** | Select seats, assign passengers, and confirm booking. |
|  | **Cancel Ticket** | Release seats back to the pool. |
|  | **Seat Availability** | Check real-time seat status. |
| **Non-Functional** | **Concurrency** | **Crucial:** No two users can book the same seat (Double Booking). |
|  | **Consistency** | ACID properties must be maintained. |
|  | **Latency** | Low latency for search and booking confirmation. |

---

### 2) Rough Flow of Program

1. **User Search:** User inputs Source `A` and Destination `B`.
2. **System Fetch:** System returns list of `Trains` with available `Seats`.
3. **Selection:** User selects a `Train` and specific `SeatType` (e.g., Sleeper, AC).
4. **Booking Request:** User submits passenger details.
5. **Concurrency Lock:** System **locks** the requested seats for this transaction.
6. **Payment:** (Simulated) Payment processed.
7. **Confirmation:** Booking status becomes `CONFIRMED`, seats marked `BOOKED`.
8. **Notification:** Ticket details returned to user.

---

### 3) Entities & Class Structure

**Entities (Core Objects):**

* **User:** `id`, `name`, `email`.
* **Train:** `trainId`, `trainName`, `source`, `destination`, `seats` (Map/List).
* **Seat:** `seatNo`, `seatType` (BERTH/SEATER), `status` (AVAILABLE/BOOKED/LOCKED).
* **Booking:** `bookingId`, `user`, `train`, `seats`, `totalAmount`, `status`.

**MVC Data Flow:**
`Client (Main)` -> `Controller (API Layer)` -> `Service (Business Logic + Locking)` -> `Repository (Data Access)` -> `Database (In-Memory Map)`

---

### 4) Relations (OOPS & SOLID)

| Principle/Concept | Application in Design |
| --- | --- |
| **Encapsulation** | All entity fields are `private` (accessed via Getters/Setters/Lombok). |
| **Single Responsibility (SRP)** | `BookingService` handles booking logic; `TrainService` handles train search; `Repository` handles data storage only. |
| **Open/Closed (OCP)** | Repository is an `interface`. We implement `InMemoryRepository` now but can add `SqlRepository` later without changing Service logic. |
| **Composition** | `Train` *has-a* list of `Seats`. `Booking` *has-a* list of `Passengers`. |

---

### 5) Design Patterns

| Pattern | Usage in this Design |
| --- | --- |
| **Singleton** | Spring Beans (`Service`, `Controller`, `Repository`) are singletons by default. |
| **Strategy** | (Optional) `FareCalculationStrategy` (Dynamic pricing vs Flat rate). |
| **Builder** | Used to construct complex `Booking` objects cleanly. |
| **Optimistic Locking** | (Concept) Using versions or synchronized blocks to handle concurrent seat booking. |

---

### 6) In-Memory Storage & DB Selection

**In-Memory Design:**
We will use `ConcurrentHashMap` to simulate tables.

* `Map<String, Train> trainTable`
* `Map<String, Booking> bookingTable`

**Database Selection Reasoning (If moving to production):**

| Feature | Recommended DB | Reason |
| --- | --- | --- |
| **Ticket Booking** | **RDBMS (PostgreSQL/MySQL)** | **ACID Compliance is non-negotiable.** We need strict Transaction management to prevent double booking. Relational models fit the structured data (Trains, Schedules). |
| **Search/Caching** | **Redis** | To cache seat availability and train routes for fast reading. |

---

### 7) Architecture

**Style:** MVC (Model-View-Controller) layered architecture.
**Framework:** Java Spring Boot.

---

### 8) Important Dependencies

| Dependency | Purpose | Why? |
| --- | --- | --- |
| **Spring Boot Starter Web** | Core Web Framework | Provides REST support, Tomcat server, and DI Container. |
| **Lombok** | Boilerplate reduction | Generates Getters, Setters, Constructors, Builders automatically. Saves coding time in interview. |
| **Spring Boot Starter Validation** | Input Validation | `@NotNull`, `@Min` annotations to sanitize requests. |

---

### 9) Code Implementation

Here is the sequential implementation.

#### A. Entities (Model)

```java

// 1. Seat Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    private String seatNo;
    private SeatStatus status; // Enum: AVAILABLE, LOCKED, BOOKED
    private double price;
}

// 2. Train Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    private String trainId;
    private String trainName;
    private String source;
    private String destination;
    private List<Seat> seats;
}

// 3. Booking Entity
@Data
@Builder
public class Booking {
    private String bookingId;
    private String trainId;
    private String userId;
    private List<Seat> bookedSeats;
    private double totalAmount;
    private BookingStatus status; // Enum: CONFIRMED, FAILED
}

// Enums
enum SeatStatus { AVAILABLE, LOCKED, BOOKED }
enum BookingStatus { CONFIRMED, FAILED }

```

#### B. DTOs (Data Transfer Objects)

```java


@Data
public class BookingRequest {
    private String userId;
    private String trainId;
    private int numberOfSeats;
}

@Data
public class BookingResponse {
    private String bookingId;
    private String status;
    private String message;
    private double amount;
}

```

#### C. Repository (Interface + In-Memory Impl)

```java


// Interface allows swapping with JPA later
public interface TrainRepository {
    Optional<Train> findById(String trainId);
    void save(Train train);
}

public interface BookingRepository {
    void save(Booking booking);
}

```

```java


@Repository
public class InMemoryTrainRepo implements TrainRepository {
    private final Map<String, Train> trainDB = new ConcurrentHashMap<>();

    @Override
    public Optional<Train> findById(String trainId) {
        return Optional.ofNullable(trainDB.get(trainId));
    }

    @Override
    public void save(Train train) {
        trainDB.put(train.getTrainId(), train);
    }
}

@Repository
public class InMemoryBookingRepo implements BookingRepository {
    private final Map<String, Booking> bookingDB = new ConcurrentHashMap<>();

    @Override
    public void save(Booking booking) {
        bookingDB.put(booking.getBookingId(), booking);
    }
}

```

#### D. Service (Business Logic)

**Constraint Note:** This is where we handle the "1 Hour Interview" Concurrency Logic using `synchronized`.

```java


@Service
public class BookingService {

    @Autowired
    private TrainRepository trainRepo;
    
    @Autowired
    private BookingRepository bookingRepo;

    // Synchronized to prevent Double Booking in a simple in-memory setup
    public synchronized BookingResponse bookTicket(BookingRequest request) {
        Train train = trainRepo.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        // 1. Check Availability
        List<Seat> availableSeats = train.getSeats().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());

        if (availableSeats.size() < request.getNumberOfSeats()) {
            return new BookingResponse(); // Return failure (simplified)
        }

        // 2. Lock/Book Seats
        List<Seat> seatsToBook = availableSeats.subList(0, request.getNumberOfSeats());
        double totalAmount = 0;
        for (Seat seat : seatsToBook) {
            seat.setStatus(SeatStatus.BOOKED);
            totalAmount += seat.getPrice();
        }

        // 3. Create Booking Record
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID().toString())
                .trainId(train.getTrainId())
                .userId(request.getUserId())
                .bookedSeats(new ArrayList<>(seatsToBook)) // Copy list
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();

        // 4. Save updates
        bookingRepo.save(booking);
        trainRepo.save(train); // Update seat status in DB

        // 5. Response
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setStatus("CONFIRMED");
        response.setMessage("Booked " + request.getNumberOfSeats() + " seats.");
        response.setAmount(totalAmount);
        
        return response;
    }
    
    public void initializeDemoData() {
        List<Seat> seats = new ArrayList<>();
        for(int i=1; i<=10; i++) {
            seats.add(new Seat("S"+i, SeatStatus.AVAILABLE, 100.0));
        }
        Train t1 = new Train("123", "Express", "BLR", "DEL", seats);
        trainRepo.save(t1);
        System.out.println(">>> Demo Data Initialized: Train 123 with 10 seats.");
    }
}

```

#### E. Controller

```java


@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest request) {
        return bookingService.bookTicket(request);
    }
}

```

#### F. Main Class (Execution Constraint)

```java


@SpringBootApplication
public class IrctcApplication {

    public static void main(String[] args) {
        // 1. Initialize Spring Context
        ConfigurableApplicationContext context = SpringApplication.run(IrctcApplication.class, args);

        // 2. Manually fetch the Service bean
        BookingService service = context.getBean(BookingService.class);

        // 3. Initialize Data
        service.initializeDemoData();

        // 4. Run Simulation Logic
        System.out.println("\n--- Starting Booking Simulation ---");
        
        // Scenario 1: User A books 3 seats
        BookingRequest userA = new BookingRequest();
        userA.setUserId("UserA");
        userA.setTrainId("123");
        userA.setNumberOfSeats(3);
        
        BookingResponse respA = service.bookTicket(userA);
        System.out.println("User A: " + respA.getMessage() + " | ID: " + respA.getBookingId());

        // Scenario 2: User B books 5 seats
        BookingRequest userB = new BookingRequest();
        userB.setUserId("UserB");
        userB.setTrainId("123");
        userB.setNumberOfSeats(5);

        BookingResponse respB = service.bookTicket(userB);
        System.out.println("User B: " + respB.getMessage() + " | ID: " + respB.getBookingId());

        // Scenario 3: User C tries to book 4 seats (Only 2 remaining)
        BookingRequest userC = new BookingRequest();
        userC.setUserId("UserC");
        userC.setTrainId("123");
        userC.setNumberOfSeats(4);

        BookingResponse respC = service.bookTicket(userC);
        System.out.println("User C: " + (respC.getBookingId() == null ? "Booking Failed (Not enough seats)" : "Success"));
        
        System.out.println("--- Simulation Ended ---");
    }
}

```

---

### 10) Optimization Ideas

| Strategy | Purpose |
| --- | --- |
| **Distributed Locking** | In a real DB (Postgres), use `SELECT ... FOR UPDATE` or Redis locks (Redlock) instead of Java `synchronized` to handle multiple server instances. |
| **Caching (Redis)** | Cache Train Search results (`Search:BLR:DEL`) with a TTL. Invalidate cache only when admin updates schedule. |
| **Database Sharding** | Shard the database based on `TrainId` or `Region` to handle massive throughput. |
| **Message Queue (Kafka)** | Decouple the "Booking" from "Notification" and "Payment Processing" to reduce response time. |
| **Pagination** | Apply pagination on Search Results (`/api/trains?page=1&size=10`) to reduce payload size. |

