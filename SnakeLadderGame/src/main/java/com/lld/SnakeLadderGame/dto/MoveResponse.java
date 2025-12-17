package com.lld.SnakeLadderGame.dto;

import com.lld.SnakeLadderGame.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveResponse {

  private String playerId;
  private int diceValue;
  private int oldPosition;
  private int newPosition;
  private String message;
  private boolean isWinner;
  private Game gameSnapshot;

}
