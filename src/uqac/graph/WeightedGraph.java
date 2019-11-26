package uqac.graph;

import java.util.*;

public class WeightedGraph {

    //Adjacency list

    private HashSet<Node> nodes = new HashSet<>();

    private boolean isDirected;

    public WeightedGraph() {
        this.isDirected = false;
    }

    public WeightedGraph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public WeightedGraph(boolean isDirected, Collection<Node> nodes) {
        this.isDirected = isDirected;
        this.nodes = new HashSet<>(nodes);
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

    public Node getRandomNode() {

        Random rand = new Random();

        int index = rand.nextInt(nodes.size());
        Iterator<Node> iter = nodes.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }

        return iter.next();
    }

    public Node getClosestNode(Vector2 pos) {

        if (nodes.size() == 0) return null;

        Node closestNode = null;
        float shortestDistance = Float.POSITIVE_INFINITY;

        for (Node node : nodes) {
            if (closestNode == null) {
                closestNode = node;
                continue;
            }
            float dist = Vector2.Distance(pos, node.position);
            if ( dist < shortestDistance) {
                // new best
                closestNode = node;
                shortestDistance = dist;
            }
        }

        return closestNode;
    }

    public HashSet<Node> getNodes() {
        return nodes;
    }

    public boolean hasNode(Node node) {
        return nodes.contains(node);
    }

}
