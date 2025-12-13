package com.lld.TicTacToe;
import com.lld.TicTacToe.entity.Game;
import com.lld.TicTacToe.entity.GameStatus;
import com.lld.TicTacToe.entity.Symbol;
import com.lld.TicTacToe.service.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class TicTacToeApplication {

  public static void main(String[] args) {
    // 1. Initialize Spring Context
    ConfigurableApplicationContext context = SpringApplication.run(TicTacToeApplication.class, args);

    // 2. Fetch Service manually
    GameService service = context.getBean(GameService.class);

    // 3. Start Interactive CLI
    Scanner scanner = new Scanner(System.in);
    System.out.println("--- WELCOME TO TIC TAC TOE ---");

    System.out.print("Enter Player 1 Name (X): ");
    String p1Name = scanner.nextLine();

    System.out.print("Enter Player 2 Name (O): ");
    String p2Name = scanner.nextLine();

    // Create Game
    Game game = service.createGame(p1Name, p2Name);
    String gameId = game.getGameId();

    System.out.println("\nGame Started! ID: " + gameId);

    // Game Loop
    while (game.getStatus() == GameStatus.IN_PROGRESS) {
      printBoard(game);

      // Determine current player name for prompt
      String currentId = game.getCurrentTurnPlayerId();
      String currentName = game.getPlayers().stream()
          .filter(p -> p.getId().equals(currentId))
          .findFirst().get().getName();

      System.out.println("\n" + currentName + "'s Turn");

      int row = -1;
      int col = -1;

      // Input Validation Loop
      while (true) {
        try {
          System.out.print("Enter Row (0-2): ");
          row = Integer.parseInt(scanner.nextLine());
          System.out.print("Enter Col (0-2): ");
          col = Integer.parseInt(scanner.nextLine());

          // Attempt Move
          game = service.makeMove(gameId, currentId, row, col);
          break; // Exit input loop if successful
        } catch (NumberFormatException e) {
          System.out.println("Invalid input! Please enter numbers.");
        } catch (RuntimeException e) {
          System.out.println("Error: " + e.getMessage() + ". Try again.");
        }
      }
    }

    // Game Over
    printBoard(game);
    System.out.println("\n--- GAME OVER ---");
    if (game.getStatus() == GameStatus.WINNER) {
      String winnerId = game.getWinnerId();
      String winnerName = game.getPlayers().stream()
          .filter(p -> p.getId().equals(winnerId))
          .findFirst().get().getName();
      System.out.println("Winner is: " + winnerName + "!");
    } else {
      System.out.println("It's a Draw!");
    }

    scanner.close();
    // context.close(); // Optional: Close context if you want the app to terminate completely
  }

  private static void printBoard(Game game) {
    System.out.println("-------------");
    Symbol[][] grid = game.getBoard().getGrid();
    for (Symbol[] row : grid) {
      System.out.print("| ");
      for (Symbol s : row) {
        System.out.print((s == null ? " " : s) + " | ");
      }
      System.out.println("\n-------------");
    }
  }
}