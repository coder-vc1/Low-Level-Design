package com.lld.Library.Management.System.entity;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
  private String id;
  private String title;
  private String author;
  private int totalCopies;
  private int availableCopies;
}
