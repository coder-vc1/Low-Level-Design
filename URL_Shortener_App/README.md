Here is the Low-Level Design (LLD) for a URL Shortener, structured specifically for a 1-hour interview setting using Java Spring Boot.

### 1\) Rough Flow of Program

1.  **Input:** Client sends a Long URL (e.g., `https://google.com/very-long-path`).
2.  **Processing:**
      * System validates the URL.
      * System generates a unique unique ID or Hash.
      * System encodes this ID into a Short Code (e.g., `abc12`).
3.  **Storage:** System maps `Short Code` \<-\> `Long URL` in the persistence layer.
4.  **Output:** System returns the Short URL (e.g., `http://short.ly/abc12`).
5.  **Retrieval:** Client hits Short URL -\> System looks up Long URL -\> System redirects (HTTP 302).

-----

### 2\) Functional & Non-Functional Requirements

| Type | Requirement |
| :--- | :--- |
| **Functional** | 1. Shorten a long URL.<br>2. Redirect from short code to original URL.<br>3. Handle invalid URLs.<br>4. (Optional) Custom alias support. |
| **Non-Functional** | 1. **Read-Heavy:** Redirections happen much more than shortening (100:1 ratio).<br>2. **Low Latency:** Redirection must be near-instant.<br>3. **Unique Keys:** No two different URLs should get the same short code collision.<br>4. **Scalable:** Handle traffic spikes. |

-----

### 3\) Entity (Properties and Methods)

We keep entities lightweight (POJO).

**Class:** `UrlMapping`

  * **Properties:**
      * `id` (Long): Unique database ID (primary key).
      * `longUrl` (String): The original URL.
      * `shortCode` (String): The generated unique string (e.g., "x7z").
      * `createdDate` (LocalDateTime): Audit.
  * **Methods:**
      * Standard Getters/Setters.
      * `isValid()`: Helper to check if URL format is correct (optional inside entity, usually in DTO/Service).

-----

### 4\) Relations (OOPS & SOLID Principles)

  * **Single Responsibility Principle (SRP):**
      * `Controller`: Handles HTTP requests/responses only.
      * `Service`: Handles the business logic (Base62 encoding, ID generation).
      * `Repository`: Handles data storage/retrieval.
  * **Dependency Inversion (DIP):**
      * The Service depends on the `UrlRepository` **interface**, not the concrete `InMemoryUrlRepository`. This makes switching to a real DB seamless.
  * **Interface Segregation:**
      * Repository interfaces are specific to data operations.

-----

### 5\) Design Patterns

  * **Repository Pattern:** To abstract the data layer (In-Memory vs. MySQL vs. Redis).
  * **Singleton Pattern:** Spring Beans (`@Service`, `@Component`) are singletons by default.
  * **Strategy Pattern (Implicit):** The logic to generate the short code (Base62 vs. MD5 vs. Random) can be swapped easily in the Service layer.

-----

### 6\) Storage Strategy (In-Memory & DB Selection)

**In-Memory Approach:**
We will use a `ConcurrentHashMap` for storage and an `AtomicLong` to simulate a database auto-increment sequence.

**Database Selection Logic:**

| Database Type | Recommended? | Reason |
| :--- | :--- | :--- |
| **NoSQL (Key-Value)**<br>*(e.g., Redis, DynamoDB)* | **Highly Recommended** | 1. The data model is simple (Key -\> Value).<br>2. Extremely fast lookups (O(1)).<br>3. Horizontally scalable for billions of records. |
| **RDBMS**<br>*(e.g., PostgreSQL, MySQL)* | **Acceptable** | 1. Good if you need strict ACID compliance or complex relations (User -\> URLs).<br>2. Can use standard Auto-Increment ID for Base62 encoding easily. |
| **Why not Graph DB?** | No | No complex relationships between data nodes exist here. |

-----

### 7\) Architecture (MVC)

[Image of MVC Architecture Diagram]

**Structure:**

  * **Controller:** `UrlShortenerController`
  * **Service:** `UrlShortenerService` (Business Logic)
  * **Repository:** `UrlRepository` (Interface) -\> `InMemoryUrlRepository` (Impl)

-----

### 8\) Important Dependencies

| Dependency | Purpose |
| :--- | :--- |
| `spring-boot-starter-web` | Provides REST API framework (Tomcat, MVC). |
| `lombok` | Reduces boilerplate code (Getters, Setters, Constructors). Helps write code faster in interviews. |
| `commons-validator` (Optional) | For strictly validating if the input string is a real URL. |

-----


### 9\) Optimization Ideas (No Code)

If asked "How do we scale this?", provide these points:

1.  **Caching (Redis):** Place Redis before the DB. Since reads are 99% of traffic, check Cache first. If miss, check DB and update Cache.
2.  **Key Generation Service (KGS):** Instead of calculating Base62 on the fly (which relies on a single ID counter and is a bottleneck), pre-generate millions of unique 6-character keys and store them in a "Unused Key" table. When a user requests, just pop one off the stack.
3.  **Database Sharding:** Shard the database based on the first character of the short code or hash of the user ID to distribute load.
4.  **Analytics:** Use Kafka to async push click events (analytics) to a data warehouse, so writing stats doesn't slow down the redirection speed.

