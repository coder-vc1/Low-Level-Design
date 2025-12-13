package com.lld.TicTacToe.entity;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class Game {
  private String gameId;
  private Board board;
  private List<Player> players;
  private GameStatus status;
  private String winnerId;
  private String currentTurnPlayerId;

  public Game(List<Player> players, int size) {
    this.gameId = UUID.randomUUID().toString();
    this.players = players;
    this.board = new Board(size);
    this.status = GameStatus.IN_PROGRESS;
    this.currentTurnPlayerId = players.get(0).getId(); // First player starts
  }
}
