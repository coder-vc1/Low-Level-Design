package com.lld.SnakeLadderGame.service;


import com.lld.SnakeLadderGame.dto.CreateGameRequest;
import com.lld.SnakeLadderGame.dto.MoveResponse;
import com.lld.SnakeLadderGame.entity.Board;
import com.lld.SnakeLadderGame.entity.Dice;
import com.lld.SnakeLadderGame.entity.Game;
import com.lld.SnakeLadderGame.entity.GameStatus;
import com.lld.SnakeLadderGame.entity.Player;
import com.lld.SnakeLadderGame.repo.GameRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;

  public Game createGame(CreateGameRequest request) {
    // 1. Setup Board
    Map<Integer, Integer> jumpers = new HashMap<>();
    if (request.getSnakes() != null) {
      jumpers.putAll(request.getSnakes());
    }
    if (request.getLadders() != null) {
      jumpers.putAll(request.getLadders());
    }

    System.out.println("Printing Jumpers 'key-value' Pair ------------->");

    for (Integer key : jumpers.keySet()) {
      System.out.println(key + ", " + jumpers.get(key));
    }

    System.out.println("Printing Jumpers ------------->");

    Board board = new Board(request.getBoardSize(), jumpers);

    // 2. Setup Players
    List<Player> players = request.getPlayerNames().stream().map(
            name -> Player.builder().id(UUID.randomUUID().toString()).name(name).position(0).build())
        .collect(Collectors.toList());

    // 3. Create Game
    Game game = Game.builder().id(UUID.randomUUID().toString()).board(board).players(players)
        .dice(new Dice(request.getDiceCount())).currentTurnIndex(0).status(GameStatus.IN_PROGRESS)
        .build();

    return gameRepository.save(game);
  }

  public MoveResponse playTurn(String gameId) {
    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Game not found"));

    if (game.getStatus() == GameStatus.FINISHED) {
      throw new IllegalStateException("Game is already finished. Winner: " + game.getWinnerId());
    }

    Player currentPlayer = game.getPlayers().get(game.getCurrentTurnIndex());

    // 1. Roll Dice
    int diceVal = game.getDice().roll();
    int oldPos = currentPlayer.getPosition();
    int nextPos = oldPos + diceVal;

    String msg = "Moved normally.";

    // 2. Check Bounds
    if (nextPos <= game.getBoard().getSize()) {

      // 3. Get Final Position
      // (Board.getNextPosition now handles the recursive recursion/loops internally)
      int finalPos = game.getBoard().getNextPosition(nextPos);

      // 4. Set Message
      if (finalPos > nextPos) {
        msg = "Boosted up (Ladder/Chain)!"; // Updated message
      } else if (finalPos < nextPos) {
        msg = "Slid down (Snake/Chain)!";   // Updated message
      }

      currentPlayer.setPosition(finalPos);

      // 5. Check Win
      if (finalPos == game.getBoard().getSize()) {
        game.setStatus(GameStatus.FINISHED);
        game.setWinnerId(currentPlayer.getId());
        msg = "WINNER!";
      }
    } else {
      msg = "Dice value too high to move.";
    }

    // 6. Rotate Turn (if not won)
    if (game.getStatus() != GameStatus.FINISHED) {
      game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % game.getPlayers().size());
    }

    gameRepository.save(game);

    return new MoveResponse(currentPlayer.getId(), diceVal, oldPos, currentPlayer.getPosition(),
        msg, game.getStatus() == GameStatus.FINISHED, game);
  }
}
