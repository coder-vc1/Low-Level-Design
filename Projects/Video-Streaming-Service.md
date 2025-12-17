---
# Low-Level Design (LLD) for a basic YouTube-like Video Streaming Service in Java.
---

### 1) Functional and Non-functional Requirements

**Functional Requirements:**

* **Upload Video:** Users can upload video metadata (title, description).
* **Play Video:** Users can fetch video details and stream (simulated).
* **Engagement:** Users can Like/Dislike videos and Comment.
* **Search:** Search videos by title.

**Non-Functional Requirements:**

* **Scalability:** The system must handle high read (view) vs. write (upload) ratio.
* **Availability:** Service should be highly available.
* **Extensibility:** Easy to switch data storage implementations.

---

### 2) Rough Flow of Program

1. **Init:** User initializes the app (Spring Context).
2. **Creation:** User creates a profile and uploads a video via `VideoService`.
3. **Storage:** `VideoRepository` saves metadata in-memory.
4. **Consumption:** Another User searches for the video or fetches it by ID.
5. **Interaction:** User adds a comment or likes the video; `VideoService` updates the entity.

---

### 3) Entities (Properties & Methods)

| Entity | Properties | Key Methods (Getters/Setters assumed) |
| --- | --- | --- |
| **User** | `id`, `username`, `email` | `subscribe()`, `uploadVideo()` |
| **Video** | `id`, `title`, `desc`, `uploaderId`, `likes`, `views`, `url` | `incrementLikes()`, `incrementViews()` |
| **Comment** | `id`, `videoId`, `userId`, `text`, `timestamp` | - |

---

### 4) Relations (OOPS & SOLID)

* **User 1:N Video:** A user can upload multiple videos.
* **Video 1:N Comment:** A video has many comments.
* **User 1:N Comment:** A user can write many comments.
* **ISP (Interface Segregation):** Use Interfaces for Services/Repos so implementation details (Storage/Logic) don't leak.
* **SRP (Single Responsibility):** `VideoService` handles business logic, `VideoRepository` handles data access.

---

### 5) Design Patterns

| Pattern | Usage in this Design |
| --- | --- |
| **Repository Pattern** | To abstract the data layer (In-Memory vs DB). |
| **Dependency Injection** | Injecting Repositories into Services (Spring IOC). |
| **Singleton** | Service and Repository beans are Singletons by default in Spring. |
| **DTO Pattern** | Using `VideoDto` to transfer data between Controller and Service. |

---

### 6) In-Memory & Database Selection Strategy

**In-Memory Implementation:**

* Use `ConcurrentHashMap<String, Entity>` for thread-safe storage.
* Use `AtomicLong` for generating unique IDs.

**Database Selection Logic:**

| Data Type | Recommended DB | Reason |
| --- | --- | --- |
| **Video Metadata** | **PostgreSQL / MySQL** | Strong ACID properties are needed for relations (User -> Video) and consistency. |
| **Video File (Blob)** | **AWS S3 / Azure Blob** | Databases are bad for large binary files. Object storage is cheaper and scalable. |
| **Comments/Likes** | **Cassandra / MongoDB** | High write throughput. Eventual consistency is acceptable for like counts. |
| **Search Index** | **ElasticSearch** | Efficient full-text search capabilities on titles/descriptions. |

---

### 7) Architecture

* **Style:** MVC (Model-View-Controller) within a Spring Boot application.
* **Flow:** Controller -> Service -> Repository -> Data Store.

---

### 8) Important Dependencies

| Dependency | Reason |
| --- | --- |
| `spring-boot-starter-web` | Provides REST support and Embedded Tomcat. |
| `lombok` | Reduces boilerplate (Getters, Setters, Constructors, Builder). |
| `spring-boot-starter-test` | For Unit testing (JUnit/Mockito). |

---

### 9) Code Implementation

**Note:** The code below follows the sequence requested. It uses an **In-Memory** implementation but is structured via interfaces to be DB-agnostic.

#### A. Entities & DTOs

```java

// --- Entities ---
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    private String id;
    private String title;
    private String description;
    private String uploaderId;
    private AtomicInteger likes = new AtomicInteger(0);
    private AtomicInteger views = new AtomicInteger(0);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String id;
    private String videoId;
    private String userId;
    private String text;
}

// --- DTOs ---
@Data
@AllArgsConstructor
public class VideoUploadDto {
    private String title;
    private String description;
    private String uploaderId;
}

```

#### B. Repository Layer (Interface + In-Memory Impl)

```java

// Interface allows easy swap to JPA/Mongo later
public interface VideoRepository {
    Video save(Video video);
    Optional<Video> findById(String id);
    List<Video> findByTitle(String title);
    List<Video> findAll();
}

@Repository
public class InMemoryVideoRepository implements VideoRepository {
    private final Map<String, Video> store = new ConcurrentHashMap<>();

    @Override
    public Video save(Video video) {
        store.put(video.getId(), video);
        return video;
    }

    @Override
    public Optional<Video> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Video> findByTitle(String title) {
        return store.values().stream()
                .filter(v -> v.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Video> findAll() {
        return new ArrayList<>(store.values());
    }
}

```

#### C. Service Layer

```java

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Video uploadVideo(VideoUploadDto dto) {
        Video video = new Video();
        video.setId(UUID.randomUUID().toString()); // Simulating DB ID generation
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setUploaderId(dto.getUploaderId());
        
        System.out.println("Uploading video: " + dto.getTitle());
        return videoRepository.save(video);
    }

    public Video playVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        video.getViews().incrementAndGet(); // Thread-safe increment
        System.out.println("Playing video: " + video.getTitle() + " | Views: " + video.getViews());
        return video;
    }

    public void likeVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        video.getLikes().incrementAndGet();
        System.out.println("Liked video: " + video.getTitle() + " | Total Likes: " + video.getLikes());
    }
    
    public List<Video> searchVideos(String keyword) {
        return videoRepository.findByTitle(keyword);
    }
}

```

#### D. Controller Layer

```java

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<Video> upload(@RequestBody VideoUploadDto dto) {
        return ResponseEntity.ok(videoService.uploadVideo(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> play(@PathVariable String id) {
        return ResponseEntity.ok(videoService.playVideo(id));
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable String id) {
        videoService.likeVideo(id);
        return ResponseEntity.ok().build();
    }
}

```

#### E. Main Class (Demo Logic)

```java

@SpringBootApplication
public class YoutubeLLDApplication {

    public static void main(String[] args) {
        // 1. Initialize Spring Context
        ConfigurableApplicationContext context = SpringApplication.run(YoutubeLLDApplication.class, args);

        // 2. Fetch Service Bean
        VideoService videoService = context.getBean(VideoService.class);

        System.out.println("--- Starting Simulation ---");

        // 3. User 1 Uploads a Video
        VideoUploadDto uploadDto = new VideoUploadDto("System Design 101", "Learn LLD", "User1");
        Video uploadedVideo = videoService.uploadVideo(uploadDto);
        System.out.println("Video Uploaded with ID: " + uploadedVideo.getId());

        // 4. User 2 Searches and Plays Video
        System.out.println("\n--- Searching & Playing ---");
        videoService.searchVideos("Design").forEach(v -> 
            System.out.println("Found: " + v.getTitle())
        );
        
        videoService.playVideo(uploadedVideo.getId());
        videoService.playVideo(uploadedVideo.getId()); // Play again to increase view count

        // 5. User 3 Likes the Video
        System.out.println("\n--- Engagement ---");
        videoService.likeVideo(uploadedVideo.getId());

        System.out.println("--- Simulation Ends ---");
    }
}

```

---

### 10) Optimization Ideas

* **Caching:** Integrate Redis to cache `Video` objects for popular videos to reduce DB (or Repo) hits.
* **Pagination:** Implement `Pageable` in the Repository `findAll` and `findByTitle` to avoid loading millions of records.
* **CDN:** Serve the actual video content (blob) via a Content Delivery Network (e.g., Cloudfront) for low latency.
* **Asynchronous Processing:** Move "Like" and "View Count" updates to a message queue (Kafka) so the user doesn't wait for the DB write.
