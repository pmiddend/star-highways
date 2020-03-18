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

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class UniverseTest {
  private final Graph<StarSystem, DefaultWeightedEdge> universe = createUniverse();

  private void assertRouteDistance(final double d, final List<StarSystem> stations) {
    assertEquals(d, Universe.routeDistance(universe, stations), 0.001);
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
    assertThrows(NoSuchRouteException.class,
            () -> Universe.routeDistance(universe, List.of(SOLAR_SYSTEM, VEGA, BETELGEUSE)));
  }

  @Test
  void exercise6() {
    final List<List<StarSystem>> cycles = new HawickJamesSimpleCycles<>(universe).findSimpleCycles()
                                                                                 .stream()
                                                                                 .filter(l -> l.contains(SIRIUS))
                                                                                 .filter(l -> l.size() <= 3)
                                                                                 .collect(Collectors.toList());
    assertTrue(cycles.contains(List.of(VEGA, SIRIUS, ALPHA_CENTAURI)));
    assertTrue(cycles.contains(List.of(BETELGEUSE, SIRIUS)));
  }

  @Test
  void exercise7() {
    final List<List<StarSystem>> paths = new AllDirectedPaths<>(universe).getAllPaths(SOLAR_SYSTEM, SIRIUS, false, 3)
                                                                         .stream()
                                                                         .map(GraphPath::getVertexList)
                                                                         // we can extend paths of size 3 by one hop
                                                                         .filter(vl -> vl.size() == 4 || vl.size() == 3)
                                                                         .collect(Collectors.toList());
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, ALPHA_CENTAURI, SIRIUS)));
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, BETELGEUSE, SIRIUS)));
    assertTrue(paths.contains(List.of(SOLAR_SYSTEM, VEGA, ALPHA_CENTAURI, SIRIUS)));
  }

  @Test
  void exercise8() {
    assertEquals(9.0, new BFSShortestPath<>(universe).getPathWeight(SOLAR_SYSTEM, SIRIUS), 0.001);
  }

  @Test
  void exercise9() {
    final Optional<List<StarSystem>> cycles = new HawickJamesSimpleCycles<>(universe).findSimpleCycles()
                                                                                     .stream()
                                                                                     .filter(l -> l.contains(
                                                                                             ALPHA_CENTAURI))
                                                                                     .peek(Collections::reverse)
                                                                                     .peek(l -> l.add(l.get(0)))
                                                                                     .min(Comparator.comparing(cycle -> Universe
                                                                                             .routeDistance(universe,
                                                                                                     cycle)));
    assertTrue(cycles.isPresent());
    assertEquals(9.0, Universe.routeDistance(universe, cycles.get()), 0.001);
  }
}