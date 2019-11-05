package uqac.graph;

import java.util.HashSet;

public class WeightedGraph {

    //Adjacency list

    HashSet<Node> nodes = new HashSet<>();

    boolean isDirected = true;

    public WeightedGraph() {

    }

    public void addNode(Node node) {

        nodes.add(node);
    }

    public void addEdge(Node source, Node destination, float cost) {

        if (!nodes.contains(source) && !nodes.contains(destination)) {
            //They do not belong both to the same graph!
            return;
        }

        source.addNeighbor(destination, cost);

        if (!isDirected) {
            //repeat it the other way around
            destination.addNeighbor(source, cost);
        }

    }

    public boolean hasNode(Node node) {
        return nodes.contains(node);
    }

}
