package uqac.graph.pathfinding;


import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.utils.Vector2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class NodeAStar implements INode {

    Node node;
    NodeAStar parent;
    float GScore = 0f; //Start to node
    float HScore = 0f; //Node to Goal (Heuristic)

    NodeAStar(Node node) {
        this.node = node;
    }

    float getFScore() {
        return HScore + GScore;
    }

    public void exploreNeighbors(Collection<NodeAStar> closedSet, Collection<NodeAStar> openSet, BiFunction<Node, Node, Float> heuristic, NodeAStar goal) {
        ArrayList<Node> neighbors = this.node.getNeighbors();

        for (Node neigh : neighbors) {

            NodeAStar neighbor = new NodeAStar(neigh);

            //if this neighbor is already in the closed list, ignore it
            if (closedSet.contains(neighbor))
                continue;

            float g = this.GScore + this.node.getCostToNeighbor(neighbor.node);

            // if it's not in the open list...
            if (!openSet.contains(neighbor))
            {
                // compute its scores, set the parent
                neighbor.GScore = g;
                neighbor.HScore = heuristic.apply(neighbor.node, goal.node);
                neighbor.parent = this;

                // and add it to the open list
                openSet.add(neighbor);

                //System.out.println("ADDED : " + neighbor.toString());
            }
            else
            {
                // test if using the current G score makes the neighbors F score
                // lower, if yes update the parent because it means it's a better path
                if (g + neighbor.HScore < neighbor.getFScore())
                {
                    neighbor.GScore = g;
                    neighbor.parent = this;
                }
            }
        }
    }

    @Override
    /**
     * Two NodeAStart are equals only if the two nodes they represent are equals.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        NodeAStar otherNode = (NodeAStar) obj;

        return node.equals(otherNode.node);
    }

    @Override
    public String toString() {

        DecimalFormat df = new DecimalFormat("0.00");

        String str = "";
        str += "(" + df.format(getPosition().x) + ", " + df.format(getPosition().y) + ")";
        //str += ", (" + GScore + ", " + HScore + ", " + getFScore() + ")";
        return str;
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public Vector2 getPosition() {
        return node.position;
    }

    @Override
    public float getCostToNeighbor(INode neighbor) {
        if (neighbor instanceof Node) {
            return node.getCostToNeighbor((Node)neighbor);
        }
        else {
            return node.getCostToNeighbor(((NodeAStar)neighbor).node);
        }
    }
}