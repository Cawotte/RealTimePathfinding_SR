package uqac.graph.pathfinding;

import uqac.graph.*;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;

public class AStar implements IRealTimePathfinding {

    //TODO : Improve algo with sorting by ascending F
    //TODO : Add Logs

    private BiFunction<Node, Node, Float> heuristic;

    private Path path = new Path(false);
    private Iterator<Node> iterator = null;

    private LogPathfinding log = new LogPathfinding();


    HashSet<NodeAStar> closedSet = new HashSet<>();

    public AStar(BiFunction<Node, Node, Float> heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Path computeFullPath(Node startNode, Node goalNode) throws PathNotFoundException
    {
        log.startLogging();

        NodeAStar start = new NodeAStar(startNode);
        NodeAStar goal = new NodeAStar(goalNode);
        NodeAStar current;

        //initialize new path
        this.path = new Path(false);

        if (start.equals(goal)) {
            path.setCompleted(true);
            log.finishLogging(path);
            return path;
        }

        closedSet = new HashSet<>();
        /*
        PriorityQueue<NodeAStar> openSet = new PriorityQueue<NodeAStar>(
                new Comparator<NodeAStar>() {
                    @Override
                    public int compare(NodeAStar node0, NodeAStar node1) {
                        return Float.compare(node0.getFScore(), node1.getFScore());
                    }
                }); */


        HashSet<NodeAStar> openSet = new HashSet<>();

        float g;

        start.GScore = 0f;
        start.HScore = heuristic.apply(start.node, goal.node);
        openSet.add(start);


        while (openSet.size() > 0) {

            current = getNodeWithMinF(openSet);

            System.out.println("CHOSEN : " + current.toString());

            closedSet.add(current);
            openSet.remove(current);

            //Goal is found, time to build the final path!
            if (current.equals(goal)) {

                goal.parent = current.parent;

                path.addNodeAtBeginning(goal.node);

                while (current.parent != null) {
                    path.addNodeAtBeginning(current.parent.node);
                    current = current.parent;
                }

                path.setCompleted(true);
                log.finishLogging(path);

                return path;
            }

            //get neighbors
            ArrayList<Node> neighbors = current.node.getNeighbors();

            for (Node neigh : neighbors) {

                NodeAStar neighbor = new NodeAStar(neigh);

                //if this neighbor is already in the closed list, ignore it
                if (closedSet.contains(neighbor))
                    continue;

                g = current.GScore + current.node.getCostToNeighbor(neighbor.node);

                // if it's not in the open list...
                if (!openSet.contains(neighbor))
                {
                    // compute its scores, set the parent
                    neighbor.GScore = g;
                    neighbor.HScore = heuristic.apply(neighbor.node, goal.node);
                    neighbor.parent = current;

                    // and add it to the open list
                    openSet.add(neighbor);
                    System.out.println("ADDED : " + neighbor.toString());
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

        log.finishLogging(path);

        throw new PathNotFoundException(startNode, goalNode);
    }

    @Override
    public void beginPathfinding(Node start, Node goal) {


        try {
            path = computeFullPath(start, goal);
        }
        catch (PathNotFoundException err) {
            System.out.println("AStar : Path not found!");
            return;
        }

        iterator = path.getNodePath().iterator();

    }

    @Override
    public Node getNextStep() {

        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public boolean hasFinished() {
        return !iterator.hasNext();
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public LogPathfinding getLog() {
        return log;
    }

    @Override
    public Collection<? extends INode> getVisited() {
        return closedSet;
    }

    private NodeAStar getNodeWithMinF(HashSet<NodeAStar> openSet) {
        NodeAStar bestNode = null;
        float minF = Float.MAX_VALUE;
        ArrayList<NodeAStar> equalsF = new ArrayList<>();

        for (NodeAStar node : openSet) {
            if (bestNode == null) {
                bestNode = node;
                minF = node.getFScore();
            }

            if (node.getFScore() < minF) {
                bestNode = node;
                minF = node.getFScore();

                equalsF = new ArrayList<>();
                equalsF.add(bestNode);
            }
            else if (node.getFScore() == minF) {

                equalsF.add(node);
            }
        }

        float minH = Float.MAX_VALUE;
        for (NodeAStar node : equalsF) {
            if (node.HScore < minH) {
                bestNode = node;
                minH = node.HScore;
            }
        }

        return bestNode;
    }

    private static class NodeAStar implements INode {

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
        public String toString() {

            DecimalFormat df = new DecimalFormat("0.00");

            String str = "";
            str += "(" + df.format(getPosition().x) + ", " + df.format(getPosition().y) + ")";
            str += ", (" + GScore + ", " + HScore + ", " + getFScore() + ")";
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
    }
}
