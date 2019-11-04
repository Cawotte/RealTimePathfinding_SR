package uqac.graph.pathfinding;

import uqac.graph.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiFunction;

public class AStar implements IPathfinding {

    //TODO : Improve algo with sorting by ascending F
    //TODO : Add Logs

    private BiFunction<Node, Node, Float> heuristic;
    private Path path = new Path(false);

    public AStar(BiFunction<Node, Node, Float> heuristic) {
        this.heuristic = heuristic;
    }

    public Path getPath(Node startNode, Node goalNode) throws PathNotFoundException {

        NodeAStar start = new NodeAStar(startNode);
        NodeAStar goal = new NodeAStar(goalNode);
        NodeAStar current;

        //initialize new path
        this.path = new Path(false);

        if (start.equals(goal)) {
            path.setCompleted(true);
            return path;
        }

        HashSet<NodeAStar> closedSet = new HashSet<>();
        HashSet<NodeAStar> openSet = new HashSet<>();
        int g = 0;

        openSet.add(start);


        while (openSet.size() > 0) {

            current = getNodeWithMinF(openSet);

            closedSet.add(current);
            openSet.remove(current);

            //Goal is found, time to build the final path!
            if (current.equals(goal)) {

                goal.parent = current.parent;

                path.addNodeAtBegin(goal.node);

                while (current.parent != null) {
                    path.addNodeAtBegin(current.parent.node);
                    current = current.parent;
                }

                path.setCompleted(true);

                return path;
            }

            //get neighbors
            ArrayList<Node> neighbors = getValidNeighbors(current);
            g++;

            for (Node neigh : neighbors) {

                NodeAStar neighbor = new NodeAStar(neigh);

                //if this neighbor is already in the closed list, ignore it
                if (closedSet.contains(neighbor))
                    continue;

                // if it's not in the open list...
                if (!openSet.contains(neighbor))
                {
                    // compute its scores, set the parent
                    neighbor.GScore = g;
                    neighbor.HScore = heuristic.apply(current.node, goal.node);
                    neighbor.parent = current;

                    // and add it to the open list
                    openSet.add(neighbor);
                }
                else
                {
                    // test if using the current G score makes the neighbors F score
                    // lower, if yes update the parent because it means it's a better path
                    if (g + neighbor.HScore < neighbor.getFScore())
                    {
                        neighbor.GScore = g;
                        neighbor.parent = current;
                    }
                }
            }
        }

        throw new PathNotFoundException(startNode, goalNode);
    }

    private ArrayList<Node> getValidNeighbors(NodeAStar node) {

        return node.node.getNeighbors();
    }

    private NodeAStar getNodeWithMinF(HashSet<NodeAStar> openSet) {
        NodeAStar bestNode = null;
        float minF = 0f;

        for (NodeAStar node : openSet) {
            if (bestNode == null) {
                bestNode = node;
                minF = node.getFScore();
            }

            if (node.getFScore() < minF) {
                bestNode = node;
                minF = node.getFScore();
            }
        }

        return bestNode;
    }

    private static class NodeAStar {

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

        @Override
        /**
         * Two NodeAStart are equals only if the two nodes they represent are equals.
         */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            NodeAStar otherNode = (NodeAStar)obj;

            return node.equals(otherNode.node);
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }
    }
}
