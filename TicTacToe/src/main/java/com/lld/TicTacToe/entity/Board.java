package com.lld.TicTacToe.entity;

import lombok.Data;

@Data
public class Board {
  private Symbol[][] grid;
  private int size;

  public Board(int size) {
    this.size = size;
    this.grid = new Symbol[size][size];
  }
}
