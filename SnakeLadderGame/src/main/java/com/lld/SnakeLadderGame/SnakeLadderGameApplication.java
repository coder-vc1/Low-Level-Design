package com.lld.SnakeLadderGame;

import com.lld.SnakeLadderGame.dto.CreateGameRequest;
import com.lld.SnakeLadderGame.dto.MoveResponse;
import com.lld.SnakeLadderGame.entity.Game;
import com.lld.SnakeLadderGame.entity.Player;
import com.lld.SnakeLadderGame.service.GameService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SnakeLadderGameApplication {

  private static CreateGameRequest getCreateGameRequest() {
    int boardSize = 100;
    int diceCount = 1;
    List<String> players = Arrays.asList("Amit", "Ram", "Kabir");

    Map<Integer, Integer> snakes = new HashMap<>();
    snakes.put(99, 5); // Big snake
    snakes.put(93, 15);
    snakes.put(19, 2);
    snakes.put(47, 11);
    snakes.put(79, 35);
    snakes.put(52, 41);

    Map<Integer, Integer> ladders = new HashMap<>();
    ladders.put(2, 20); // Small ladder
    ladders.put(22, 80);
    ladders.put(11, 93);
    ladders.put(41, 95);
    ladders.put(32, 81);
    ladders.put(5, 21);

    CreateGameRequest request = new CreateGameRequest(boardSize, diceCount, players, snakes,
        ladders);
    return request;
  }

  public static void main(String[] args) {

    // 1. Initialize Spring Context
    ConfigurableApplicationContext context = SpringApplication.run(SnakeLadderGameApplication.class,
        args);

    // 2. Fetch Service manually
    GameService service = context.getBean(GameService.class);

    System.out.println("----- STARTING DEMO SIMULATION -----");

    // 3. Setup Demo Data (P=10, Q=10 => Size 100, 1 Dice)
    CreateGameRequest request = getCreateGameRequest();

    Game game = service.createGame(request);
    String gameId = game.getId();
    System.out.println("Game Created: " + gameId);

    // 4. Simulate Turns until Win (or limit loop to prevent infinite runs in demo)
    boolean gameWon = false;
    int turns = 0;

    while (!gameWon && turns < 100) { // Safety limit

      MoveResponse move = service.playTurn(gameId);

      String playerName = game.getPlayerNameByID(move.getPlayerId());

      System.out.println(
          "Turn " + turns++ + ": Player " + playerName + " rolled " + move.getDiceValue() + ". "
              + move.getOldPosition() + " -> " + move.getNewPosition() + ". (" + move.getMessage()
              + ")");

      if (move.isWinner()) {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("!!! GAME OVER !!! Winner: " + playerName);
        System.out.println("------------------------------------------");
        gameWon = true;
      }
    }

    if (!gameWon) {
      System.out.println("Simulation ended (turn limit reached).");
    }
  }


}
