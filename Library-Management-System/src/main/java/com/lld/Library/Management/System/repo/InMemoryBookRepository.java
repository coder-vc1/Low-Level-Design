package com.lld.Library.Management.System.repo;
import com.lld.Library.Management.System.entity.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBookRepository implements BookRepository {

  // Simulating DB Table
  private final Map<String, Book> bookTable = new ConcurrentHashMap<>();

  @Override
  public Book save(Book book) {
    if(book.getId() == null) {
      book.setId(UUID.randomUUID().toString());
    }
    bookTable.put(book.getId(), book);
    return book;
  }

  @Override
  public Optional<Book> findById(String id) {
    return Optional.ofNullable(bookTable.get(id));
  }

  @Override
  public List<Book> findAll() {
    return new ArrayList<>(bookTable.values());
  }
}