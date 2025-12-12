package com.lld.Library.Management.System.dto;

import lombok.*;

@Data
public class BookRequest {
  private String title;
  private String author;
  private int copies;
  
}