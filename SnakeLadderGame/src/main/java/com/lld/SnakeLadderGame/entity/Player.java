package com.lld.SnakeLadderGame.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {

  private String id;
  private String name;
  private int position;
}
