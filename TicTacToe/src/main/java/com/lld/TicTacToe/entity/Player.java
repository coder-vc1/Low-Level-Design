package com.lld.TicTacToe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
  private String id;
  private String name;
  private Symbol symbol;
}