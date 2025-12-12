package com.lld.Library.Management.System.entity;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
public class Transaction {
  private String id;
  private String bookId;
  private String memberId;
  private LocalDate issueDate;
  private LocalDate returnDate; // Null if not returned
}