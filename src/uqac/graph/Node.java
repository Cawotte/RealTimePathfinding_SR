package uqac.graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Noeud de graph à poids
 */
public class Node {

    public Vector2 position;

    private Hashtable<Node, Float> neighbors;

    public Node() {

    }

    public Node(Hashtable<Node, Float> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Node node, float cost) {
        neighbors.put(node, cost);
    }

    public boolean hasNeighbor(Node neighbor) {
        return neighbors.containsKey(neighbor);
    }

    public float getCostToNeighbor(Node neighbor) {
        return neighbors.get(neighbor);
    }

    public ArrayList<Node> getNeighbors() {
        return Collections.list(neighbors.keys());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node otherNode = (Node)o;

        return position.equals(otherNode.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
