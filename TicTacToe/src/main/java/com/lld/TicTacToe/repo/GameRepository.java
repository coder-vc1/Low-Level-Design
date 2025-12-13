package com.lld.TicTacToe.repo;

import com.lld.TicTacToe.entity.Game;
import java.util.Optional;

public interface GameRepository {
  Game save(Game game);
  Optional<Game> findById(String gameId);
}