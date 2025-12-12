package com.lld.Library.Management.System.dto;

import lombok.Data;

@Data
public class BorrowRequest {
  private String memberId;
  private String bookId;
}
