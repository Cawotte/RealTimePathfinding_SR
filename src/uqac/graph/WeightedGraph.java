package uqac.graph;

import java.util.*;

public class WeightedGraph {

    //Adjacency list

    private HashSet<Node> nodes = new HashSet<>();

    private Vector2 minBounds;
    private Vector2 maxBounds;

    public WeightedGraph() {
        this.minBounds = new Vector2();
        this.maxBounds = new Vector2();
    }

    public void addNode(Node node) {

        //new min x ?
        if (node.position.x < minBounds.x) {
            minBounds.x = node.position.x;
        }
        //new max x ?
        else if (node.position.x > maxBounds.x) {
            maxBounds.x = node.position.x;
        }

        //new min y ?
        if (node.position.y < minBounds.y) {
            minBounds.y = node.position.y;
        }
        //new max y ?
        else if (node.position.y > maxBounds.y) {
            maxBounds.y = node.position.y;
        }

        nodes.add(node);
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

    public Vector2 getMinBound() {
        return minBounds;
    }

    public Vector2 getMaxBound() {
        return maxBounds;
    }

    public HashSet<Node> getNodes() {
        return nodes;
    }

    public boolean hasNode(Node node) {
        return nodes.contains(node);
    }

}
