package com.lld.TicTacToe.service;

import com.lld.TicTacToe.entity.Board;
import com.lld.TicTacToe.entity.Game;
import com.lld.TicTacToe.entity.GameStatus;
import com.lld.TicTacToe.entity.Player;
import com.lld.TicTacToe.entity.Symbol;
import com.lld.TicTacToe.repo.GameRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;

  public Game createGame(String p1Name, String p2Name) {
    Player p1 = new Player(UUID.randomUUID().toString(), p1Name, Symbol.X);
    Player p2 = new Player(UUID.randomUUID().toString(), p2Name, Symbol.O);

    List<Player> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);

    Game game = new Game(players, 3);
    return gameRepository.save(game);
  }

  public Game makeMove(String gameId, String playerId, int row, int col) {
    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new RuntimeException("Game not found"));

    if (game.getStatus() != GameStatus.IN_PROGRESS) {
      throw new RuntimeException("Game is already finished");
    }

    if (!game.getCurrentTurnPlayerId().equals(playerId)) {
      throw new RuntimeException("Not your turn!");
    }

    Board board = game.getBoard();
    if (board.getGrid()[row][col] != null) {
      throw new RuntimeException("Cell already occupied");
    }

    // 1. Update Board
    Symbol currentSymbol = game.getPlayers().stream()
        .filter(p -> p.getId().equals(playerId))
        .findFirst().get().getSymbol();

    board.getGrid()[row][col] = currentSymbol;

    // 2. Check Win
    if (checkWin(board, currentSymbol, row, col)) {
      game.setStatus(GameStatus.WINNER);
      game.setWinnerId(playerId);
    } else if (checkDraw(board)) {
      game.setStatus(GameStatus.DRAW);
    } else {
      // 3. Switch Turn
      String nextPlayerId = game.getPlayers().stream()
          .filter(p -> !p.getId().equals(playerId))
          .findFirst().get().getId();
      game.setCurrentTurnPlayerId(nextPlayerId);
    }

    return gameRepository.save(game);
  }

  private boolean checkWin(Board board, Symbol symbol, int row, int col) {
    int n = board.getSize();
    Symbol[][] grid = board.getGrid();

    // Check Row
    boolean rowWin = true;
    for (int i = 0; i < n; i++) if (grid[row][i] != symbol) rowWin = false;

    // Check Col
    boolean colWin = true;
    for (int i = 0; i < n; i++) if (grid[i][col] != symbol) colWin = false;

    // Check Main Diagonal
    boolean diagWin = true;
    if(row == col) {
      for (int i = 0; i < n; i++) if (grid[i][i] != symbol) diagWin = false;
    } else {
      diagWin = false;
    }

    // Check Anti Diagonal
    boolean antiDiagWin = true;
    if(row + col == n - 1) {
      for (int i = 0; i < n; i++) if (grid[i][n - 1 - i] != symbol) antiDiagWin = false;
    } else {
      antiDiagWin = false;
    }

    return rowWin || colWin || diagWin || antiDiagWin;
  }

  private boolean checkDraw(Board board) {
    for(Symbol[] row : board.getGrid()) {
      for(Symbol s : row) {
        if(s == null) return false;
      }
    }
    return true;
  }

  public Game getGame(String gameId) {
    return gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));
  }
}