package com.lld.Library.Management.System.service;

import com.lld.Library.Management.System.entity.Book;
import com.lld.Library.Management.System.repo.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final BookRepository bookRepository;
  // Assume MemberRepository and TransactionRepository are injected here

  public
  Book addBook(String title, String author, int copies) {
    Book book = Book.builder()
        .title(title)
        .author(author)
        .totalCopies(copies)
        .availableCopies(copies)
        .build();
    return bookRepository.save(book);
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  // Synchronized to prevent race conditions during interviews (or use AtomicInteger)
  public synchronized String borrowBook(String bookId, String memberId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Book not found"));

    if (book.getAvailableCopies() <= 0) {
      return "FAILURE: Book not available";
    }

    // Update Book
    book.setAvailableCopies(book.getAvailableCopies() - 1);
    bookRepository.save(book);

    // Create Transaction (Logic simplified for brevity)
    // Transaction txn = Transaction.builder().bookId(bookId).memberId(memberId).build();
    // transactionRepository.save(txn);

    return "SUCCESS: Book borrowed. Remaining: " + book.getAvailableCopies();
  }
}