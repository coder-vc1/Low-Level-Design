package com.lld.TicTacToe.dto;

import lombok.Data;

@Data
public class ConnectGameRequest {
  private String player1Name;
  private String player2Name;
}