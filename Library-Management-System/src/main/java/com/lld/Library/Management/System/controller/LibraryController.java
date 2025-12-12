package com.lld.Library.Management.System.controller;


import com.lld.Library.Management.System.dto.BookRequest;
import com.lld.Library.Management.System.entity.Book;
import com.lld.Library.Management.System.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

  private final LibraryService libraryService;

  @PostMapping("/books")
  public Book addBook(@RequestBody BookRequest request) {
    return libraryService.addBook(request.getTitle(), request.getAuthor(), request.getCopies());
  }

  @GetMapping("/books")
  public List<Book> getBooks() {
    return libraryService.getAllBooks();
  }

  // Borrow endpoint (POST) would go here
}