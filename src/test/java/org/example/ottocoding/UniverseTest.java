package org.example.ottocoding;

import static org.example.ottocoding.StarSystem.ALPHA_CENTAURI;
import static org.example.ottocoding.StarSystem.BETELGEUSE;
import static org.example.ottocoding.StarSystem.SIRIUS;
import static org.example.ottocoding.StarSystem.SOLAR_SYSTEM;
import static org.example.ottocoding.StarSystem.VEGA;
import static org.example.ottocoding.Universe.createUniverse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

class UniverseTest {
  private final Universe universe = createUniverse();

  // A little helper function to reduce duplication in the tests
  private void assertRouteDistance(final double d, final List<StarSystem> stations) {
    assertEquals(d, universe.routeDistance(stations), 0.001);
  }

  @Test
  void exercise1() {
    assertRouteDistance(9.0, List.of(SOLAR_SYSTEM, ALPHA_CENTAURI, SIRIUS));
  }

  @Test
  void exercise2() {
    assertRouteDistance(5.0, List.of(SOLAR_SYSTEM, BETELGEUSE));
  }

  @Test
  void exercise3() {
    assertRouteDistance(13.0, List.of(SOLAR_SYSTEM, BETELGEUSE, SIRIUS));
  }

  @Test
  void exercise4() {
    assertRouteDistance(22.0, List.of(SOLAR_SYSTEM, VEGA, ALPHA_CENTAURI, SIRIUS, BETELGEUSE));
  }

  @Test
  void exercise5() {
    assertThrows(NoSuchRouteException.class, () -> universe.routeDistance(List.of(SOLAR_SYSTEM, VEGA, BETELGEUSE)));
  }

  @Test
  void exercise6() {
    final List<List<StarSystem>> paths = this.universe.allRoutes(SIRIUS,
            (route) -> route.size() > 4,
            (route) -> route.get(route.size() - 1) == SIRIUS);
    assertEquals(2, paths.size());
    assertTrue(paths.contains(List.of(SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, BETELGEUSE, SIRIUS)));
  }

  @Test
  void exercise7() {
    final List<List<StarSystem>> paths = this.universe.allRoutes(SOLAR_SYSTEM,
            (route) -> route.size() > 5,
            (route) -> route.get(route.size() - 1) == SIRIUS && route.size() == 5);
    assertEquals(3, paths.size());
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, ALPHA_CENTAURI, SIRIUS, BETELGEUSE, SIRIUS)));
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, BETELGEUSE, SIRIUS, BETELGEUSE, SIRIUS)));
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, BETELGEUSE, VEGA, ALPHA_CENTAURI, SIRIUS)));
  }

  @Test
  void exercise8() {
    assertEquals(9.0, new BFSShortestPath<>(universe.getGraph()).getPathWeight(SOLAR_SYSTEM, SIRIUS), 0.001);
  }

  @Test
  void exercise9() {
    // Find all cycles, take the one containing alpha centauri with the smallest travel time.
    final Optional<List<StarSystem>> cycles = new HawickJamesSimpleCycles<>(universe.getGraph()).findSimpleCycles()
                                                                                                .stream()
                                                                                                .filter(l -> l.contains(
                                                                                                        ALPHA_CENTAURI))
                                                                                                .peek(Collections::reverse)
                                                                                                .peek(l -> l.add(l.get(0)))
                                                                                                .min(Comparator.comparing(
                                                                                                        universe::routeDistance));
    assertTrue(cycles.isPresent());
    assertEquals(9.0, universe.routeDistance(cycles.get()), 0.001);
  }

  @Test
  void exercise10() {
    final List<List<StarSystem>> paths = this.universe.allRoutes(SIRIUS,
            (route) -> universe.routeDistance(route) >= 30,
            (route) -> route.get(route.size() - 1) == SIRIUS);
    assertEquals(7, paths.size());
    assertTrue(paths.contains(List.of(SIRIUS, BETELGEUSE, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS, BETELGEUSE, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, BETELGEUSE, SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, BETELGEUSE, VEGA, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS, VEGA, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(
            SIRIUS,
            VEGA,
            ALPHA_CENTAURI,
            SIRIUS,
            VEGA,
            ALPHA_CENTAURI,
            SIRIUS,
            VEGA,
            ALPHA_CENTAURI,
            SIRIUS)));
  }
}