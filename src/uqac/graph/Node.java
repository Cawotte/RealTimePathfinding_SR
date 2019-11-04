package uqac.graph;

import java.util.ArrayList;

/**
 * Interface représentant un noeud, tout les noeuds doivent l'implémenter.
 */
public class Node {

    private float cost;
    private ArrayList<Node> neighbors;

    public Node(float cost, ArrayList<Node> neighbors) {
        this.cost = cost;
        this.neighbors = neighbors;
    }

    public float getCost() {
        return cost;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }
}
