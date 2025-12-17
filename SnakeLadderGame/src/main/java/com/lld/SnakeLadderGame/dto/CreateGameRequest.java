package com.lld.SnakeLadderGame.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateGameRequest {

  private int boardSize; // P*Q
  private int diceCount;
  private List<String> playerNames;
  private Map<Integer, Integer> snakes;  // Start -> End
  private Map<Integer, Integer> ladders; // Start -> End

}
