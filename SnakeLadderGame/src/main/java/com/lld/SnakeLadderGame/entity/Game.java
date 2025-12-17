package com.lld.SnakeLadderGame.entity;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {

  private String id;
  private Board board;
  private List<Player> players;
  private Dice dice;
  private int currentTurnIndex; // Index of player in list
  private String winnerId;
  private GameStatus status;

  public String getPlayerNameByID(String id) {
    return this.getPlayers().stream()
        .filter(player -> player.getId().equals(id))
        .map(Player::getName)
        .findFirst()
        .orElse("Unknown Player");
  }


}
