package com.lld.Library.Management.System.repo;

import com.lld.Library.Management.System.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
  Book save(Book book);
  Optional<Book> findById(String id);
  List<Book> findAll();
}