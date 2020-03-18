package org.example.ottocoding;

public enum StarSystem {
  SOLAR_SYSTEM("Solar System"),
  ALPHA_CENTAURI("Alpha Centauri"),
  SIRIUS("Sirius"),
  BETELGEUSE("Betelgeuse"),
  VEGA("Vega");

  private final String text;

  StarSystem(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
