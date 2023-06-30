package co.fwoar;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Main implements Runnable {
    private final Map<GraphNode, Set<GraphEdge>> edgeCache = new HashMap<>();

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        var importer = new DOTImporter<Vertex, DefaultEdge>();
        importer.setVertexFactory(Vertex::new);
        Graph<Vertex, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        importer.importGraph(g, Objects.requireNonNull(getClass().getClassLoader()
                                                                 .getResourceAsStream("architecture.dot")));
        var it = new TopologicalOrderIterator<>(g);
        Iterable<Vertex> ite = () -> it;
        for (var node : ite) {
            setupNode(node);
            for (var edge : g.incomingEdgesOf(node)) {
                var from = g.getEdgeSource(edge);
                from.connectTo(node);
            }
            System.out.println();
        }
    }

    private void setupNode(Vertex node) {
        System.out.format("node> %s\n", node.label());
    }

    private static final class Vertex {
        private String label;

        private Vertex(String s) {
            this.label = s;
        }

        public String label() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return "Vertex[" + "label=" + label + ']';
        }

        public void connectTo(Vertex node) {
            System.out.printf("\t%s -> %s\n", this, node);
        }
    }
}