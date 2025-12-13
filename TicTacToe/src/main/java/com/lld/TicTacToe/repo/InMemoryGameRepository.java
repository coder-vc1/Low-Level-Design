package com.lld.TicTacToe.repo;

import com.lld.TicTacToe.entity.Game;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
  private final Map<String, Game> gameStore = new ConcurrentHashMap<>();

  @Override
  public Game save(Game game) {
    gameStore.put(game.getGameId(), game);
    return game;
  }

  @Override
  public Optional<Game> findById(String gameId) {
    return Optional.ofNullable(gameStore.get(gameId));
  }
}