package com.lld.TicTacToe.dto;

import lombok.Data;

@Data
public class GameplayRequest {
  private String gameId;
  private String playerId;
  private int row;
  private int col;
}