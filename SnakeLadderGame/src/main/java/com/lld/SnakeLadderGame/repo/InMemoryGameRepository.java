package com.lld.SnakeLadderGame.repo;

import com.lld.SnakeLadderGame.entity.Game;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryGameRepository implements GameRepository {

  private final Map<String, Game> store = new ConcurrentHashMap<>();


  @Override
  public Game save(Game game) {
    store.put(game.getId(), game);
    return game;
  }

  @Override
  public Optional<Game> findById(String id) {
    return Optional.ofNullable(store.get(id));
  }
}
