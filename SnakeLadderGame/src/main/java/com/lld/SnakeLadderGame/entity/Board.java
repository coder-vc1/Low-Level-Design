package com.lld.SnakeLadderGame.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

  private int size;
  private Map<Integer, Integer> jumpers;

  public int getNextPosition(int currentPos) {
    int next = currentPos;
    // Use a loop to handle chains: e.g., 99 (Snake) -> 5 (Ladder) -> 21
    // Safety: Use a set to detect infinite loops (e.g., 2->5 and 5->2)
    Set<Integer> visited = new HashSet<>();

    while (jumpers.containsKey(next)) {
      if (visited.contains(next)) {
        // Infinite loop detected (Bad map config), break to prevent crash
        break;
      }
      visited.add(next);
      next = jumpers.get(next);
    }
    return next;
  }


}


/*
    private int size; // P * Q
    // Map of Start -> End (Covers Snakes and Ladders)
    // e.g., 99 -> 10 (Snake), 5 -> 25 (Ladder)
    private Map<Integer, Integer> jumpers;

    public int getNextPosition(int currentPos) {
        // If currentPos matches a jumper start, return end; else return current
        return jumpers.getOrDefault(currentPos, currentPos);
    }
 */
