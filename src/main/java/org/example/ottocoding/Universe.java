package org.example.ottocoding;

import static org.example.ottocoding.StarSystem.ALPHA_CENTAURI;
import static org.example.ottocoding.StarSystem.BETELGEUSE;
import static org.example.ottocoding.StarSystem.SIRIUS;
import static org.example.ottocoding.StarSystem.SOLAR_SYSTEM;
import static org.example.ottocoding.StarSystem.VEGA;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

public class Universe {

  public static Graph<StarSystem, DefaultWeightedEdge> createUniverse() {
    Graph<StarSystem, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<StarSystem, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);
    g.addVertex(SOLAR_SYSTEM);
    g.addVertex(ALPHA_CENTAURI);
    g.addVertex(SIRIUS);
    g.addVertex(BETELGEUSE);
    g.addVertex(VEGA);
    g.setEdgeWeight(g.addEdge(SOLAR_SYSTEM, ALPHA_CENTAURI), 5.0);
    g.setEdgeWeight(g.addEdge(ALPHA_CENTAURI, SIRIUS), 4.0);
    g.setEdgeWeight(g.addEdge(SIRIUS, BETELGEUSE), 8.0);
    g.setEdgeWeight(g.addEdge(BETELGEUSE, SIRIUS), 8.0);
    g.setEdgeWeight(g.addEdge(BETELGEUSE, VEGA), 6.0);
    g.setEdgeWeight(g.addEdge(SOLAR_SYSTEM, BETELGEUSE), 5.0);
    g.setEdgeWeight(g.addEdge(SIRIUS, VEGA), 2.0);
    g.setEdgeWeight(g.addEdge(VEGA, ALPHA_CENTAURI), 3.0);
    g.setEdgeWeight(g.addEdge(SOLAR_SYSTEM, VEGA), 7.0);
    return g;
  }

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

  public static double routeDistance(final Graph<StarSystem, DefaultWeightedEdge> g, final List<StarSystem> stations) {
    return pairStream(stations).mapToDouble(p -> {
      final DefaultWeightedEdge e = g.getEdge(p.getA(), p.getB());
      if (e == null)
        throw new NoSuchRouteException(p.getA(), p.getB());
      return g.getEdgeWeight(e);
    }).sum();
  }
}
