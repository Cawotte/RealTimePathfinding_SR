package uqac.graph;

import java.util.HashMap;
import java.util.Hashtable;

public class WeightedGraph {

    Hashtable<Node, Hashtable<Node, Float>> adjacencyMatrix = new Hashtable<>();

    boolean isDirected = true;

    /*
        hashthable.putIfAbsent return null if it was absent
        hashtable.get returns null if it was absent
     */

    public void addNode(Node node) {

        Hashtable<Node, Float> currentValue;

        //If the node is not in the matrix, add it
        currentValue = adjacencyMatrix.putIfAbsent(node, new Hashtable<>());

        //Add the node to the adjacency matrix of other node
            //null if it was absent
        if (currentValue == null )  {

            //For each node, add the node to the list of adjacency of that node, with no cost (= no adjacency)
            adjacencyMatrix.forEach( (k, v) -> {
                v.putIfAbsent(node, 0f);
            }
            );
        }

    }

    public void addEdge(Node source, Node destination, float cost) {

        Hashtable<Node, Float> nodeAdjacency = adjacencyMatrix.get(source);
        nodeAdjacency.put(destination, cost);
        source.addNeighbhor(destination);

        //If the edge already exists, update the cost with the new cost.

        if (!isDirected) {

            //repeat it the other way around
            nodeAdjacency = adjacencyMatrix.get(destination);
            nodeAdjacency.put(source, cost);
            destination.addNeighbhor(source);
        }

    }

    public boolean hasEdge(Node source, Node destination) {
        return hasEdge(source, destination, 0f);
    }

    /**
     * Return true if there's an edge between source and destination. If yes store
     * the result in returnEdgeCost.
     * @param source
     * @param destination
     * @param returnEdgeCost
     * @return
     */
    public boolean hasEdge(Node source, Node destination, float returnEdgeCost) {
        Hashtable<Node, Float> nodeAdjacency = adjacencyMatrix.get(source);

        if (nodeAdjacency == null)  {
            returnEdgeCost = 0f;
            return false; //The node is not even in the graph!
        }

        returnEdgeCost = nodeAdjacency.get(destination);

        return (returnEdgeCost != 0f);
    }

    public float getEdgeCost(Node source, Node destination) {

        float cost = 0f;

        hasEdge(source, destination, cost);

        return cost;
    }
}
