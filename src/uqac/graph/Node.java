package uqac.graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Noeud de graph à poids
 */
public class Node {


    public Vector2 position;

    private Hashtable<Node, Float> neighbors = new Hashtable<>();

    public Node(float x, float y) {

        this.position = new Vector2(x,y);
    }


    /***
     * Add the node as a neighbor of the current node,
     * with the given cost to travel between them.
     * @param node
     * @param cost
     */
    public void addNeighbor(Node node, float cost) {

        neighbors.put(node, cost);
    }

    /**
     * Add the node as a neighbor of the current node.
     * Use the Euclidian distance for the cost.
     * @param node
     */
    public void addNeighbor(Node node) {
        float cost = Vector2.Distance(position, node.position);
        addNeighbor(node, cost);
    }



    /**
     * Add the node as a neighbor of the current node.
     * Use the Euclidian distance for the cost.
     * @param node
     */
    public void addNeighbor(Node node, boolean isDirected) {
        float cost = Vector2.Distance(position, node.position);
        addNeighbor(node, cost);
        if (!isDirected) {
            node.addNeighbor(this, cost);
        }
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
