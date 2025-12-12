package com.lld.Library.Management.System;

import com.lld.Library.Management.System.entity.Book;
import com.lld.Library.Management.System.service.LibraryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LibraryManagementApplication {

  public static void main(String[] args) {
    // 1. Initialize Spring Context
    ConfigurableApplicationContext context = SpringApplication.run(LibraryManagementApplication.class, args);

    // 2. Fetch Service Bean manually
    LibraryService service = context.getBean(LibraryService.class);

    System.out.println("---------- SIMULATION STARTED ----------");

    // 3. Demo Logic
    // A. Add Books
    Book b1 = service.addBook("Clean Code", "Robert Martin", 2);
    Book b2 = service.addBook("System Design", "Alex Xu", 1);

    System.out.println("Books Added: " + service.getAllBooks().size());

    // B. Simulate Borrowing
    System.out.println("User 1 borrows Clean Code: " + service.borrowBook(b1.getId(), "user1"));
    System.out.println("User 2 borrows Clean Code: " + service.borrowBook(b1.getId(), "user2"));
    System.out.println("User 2 borrows Clean Code: " + service.borrowBook(b1.getId(), "user2"));
    System.out.println("User 3 borrows Clean Code: " + service.borrowBook(b1.getId(), "user3")); // Should fail

    System.out.println("---------- SIMULATION ENDED ----------");
  }

}
