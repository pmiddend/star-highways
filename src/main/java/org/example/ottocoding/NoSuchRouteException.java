package org.example.ottocoding;

public class NoSuchRouteException extends RuntimeException {
  private final StarSystem currentStarSystem;
  private final StarSystem nextStarSystem;

  public NoSuchRouteException(StarSystem currentStarSystem, StarSystem nextStarSystem) {
    super("no route between "+currentStarSystem+" and "+nextStarSystem);
    this.currentStarSystem = currentStarSystem;
    this.nextStarSystem = nextStarSystem;
  }

  public StarSystem getNextStarSystem() {
    return nextStarSystem;
  }

  public StarSystem getCurrentStarSystem() {
    return currentStarSystem;
  }
}
