package org.example.ottocoding;

import static org.example.ottocoding.StarSystem.ALPHA_CENTAURI;
import static org.example.ottocoding.StarSystem.BETELGEUSE;
import static org.example.ottocoding.StarSystem.SIRIUS;
import static org.example.ottocoding.StarSystem.SOLAR_SYSTEM;
import static org.example.ottocoding.StarSystem.VEGA;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Universe {
  private final Graph<StarSystem, DefaultWeightedEdge> graph;

  public Universe() {
    this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    this.graph.addVertex(SOLAR_SYSTEM);
    this.graph.addVertex(ALPHA_CENTAURI);
    this.graph.addVertex(SIRIUS);
    this.graph.addVertex(BETELGEUSE);
    this.graph.addVertex(VEGA);
  }

  public void addEdge(final StarSystem from, final StarSystem to, final double travelTime) {
    this.graph.setEdgeWeight(this.graph.addEdge(from, to), travelTime);
  }

  /**
   * Create the universe given in the task description
   *
   * @return See above
   */
  public static Universe createUniverse() {
    final Universe result = new Universe();
    result.addEdge(SOLAR_SYSTEM, ALPHA_CENTAURI, 5.0);
    result.addEdge(ALPHA_CENTAURI, SIRIUS, 4.0);
    result.addEdge(SIRIUS, BETELGEUSE, 8.0);
    result.addEdge(BETELGEUSE, SIRIUS, 8.0);
    result.addEdge(BETELGEUSE, VEGA, 6.0);
    result.addEdge(SOLAR_SYSTEM, BETELGEUSE, 5.0);
    result.addEdge(SIRIUS, VEGA, 2.0);
    result.addEdge(VEGA, ALPHA_CENTAURI, 3.0);
    result.addEdge(SOLAR_SYSTEM, VEGA, 7.0);
    return result;
  }

  public Graph<StarSystem, DefaultWeightedEdge> getGraph() {
    return graph;
  }

  private List<List<StarSystem>> extendRoutes(
          final List<StarSystem> currentRoute, final Predicate<List<StarSystem>> stopPredicate) {
    // So we can find an edge "from" something later on
    assert !currentRoute.isEmpty();
    final List<List<StarSystem>> result = new ArrayList<>();
    // We could allow the route with a single node, but it doesn't make that much sense
    if (currentRoute.size() > 1)
      result.add(currentRoute);
    for (final DefaultWeightedEdge e : graph.outgoingEdgesOf(currentRoute.get(currentRoute.size() - 1))) {
      final List<StarSystem> newResult = new ArrayList<>(currentRoute);
      newResult.add(this.graph.getEdgeTarget(e));
      if (!stopPredicate.test(newResult))
        result.addAll(extendRoutes(newResult, stopPredicate));
    }
    return result;
  }

  /**
   * Calculate all possible routes, starting at a star system and applying a stop predicate so we don't have an infinite loop
   * @param start Starting star system
   * @param stopPredicate When to stop extending a route
   * @param filterPredicate Filter for the resulting routes
   * @return See above
   */
  public List<List<StarSystem>> allRoutes(
          final StarSystem start,
          final Predicate<List<StarSystem>> stopPredicate,
          final Predicate<List<StarSystem>> filterPredicate) {
    return extendRoutes(Collections.singletonList(start), stopPredicate).stream()
                                                                        .filter(filterPredicate)
                                                                        .collect(Collectors.toList());
  }

  /**
   * Helper function to generate a sequence of adjacent pairs from an iterable
   *
   * @param iterable An iterable
   * @param <A>      Value type of the iterable
   * @return A stream of adjacent pairs (can be empty if the iterable doesn't have at least two elements)
   */
  private static <A> Stream<Pair<A, A>> pairStream(final Iterable<A> iterable) {
    Iterator<A> it = iterable.iterator();
    if (!it.hasNext())
      return Stream.empty();
    A current = it.next();
    Stream.Builder<Pair<A, A>> builder = Stream.builder();
    while (it.hasNext()) {
      final A next = it.next();
      builder.add(new Pair<>(current, next));
      current = next;
    }
    return builder.build();
  }

  /**
   * Given a list of waypoints, calculate the total travel distance on that route
   *
   * @param stations Intermediate stations
   * @return The sum of the edge weights
   * @throws NoSuchRouteException if we cannot find a route between two star systems
   */
  public double routeDistance(final List<StarSystem> stations) {
    return pairStream(stations).mapToDouble(p -> {
      final DefaultWeightedEdge e = this.graph.getEdge(p.getA(), p.getB());
      if (e == null)
        throw new NoSuchRouteException(p.getA(), p.getB());
      return this.graph.getEdgeWeight(e);
    }).sum();
  }
}
