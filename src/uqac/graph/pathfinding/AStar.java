package uqac.graph.pathfinding;

import uqac.graph.*;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.*;
import java.util.function.BiFunction;

public class AStar implements IRealTimePathfinding {

    //TODO : Improve algo with sorting by ascending F
    //TODO : Add Logs

    private BiFunction<Node, Node, Float> heuristic;

    private Path<Node> path = new Path<>(false);
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
        this.path = new Path<>(false);

        if (start.equals(goal)) {
            path.setCompleted(true);
            log.finishLogging(path);
            return path;
        }

        closedSet = new HashSet<>();

        /**
         * PriorityQueue
         * On donne une fonction de comparaison, et va pick celui avec la priorité
         * la plus élevé dès qu'on veut prendre un objet dans la collection.
         *
         * Ici, on prend le Node avec le F le plus grand, et en cas d'égalités,
         * tranche avec le H le plus grand.
         */
        PriorityQueue<NodeAStar> openSet = new PriorityQueue<NodeAStar>(
                new Comparator<NodeAStar>() {
                    @Override
                    public int compare(NodeAStar node0, NodeAStar node1) {
                        int comp = Float.compare(node0.getFScore(), node1.getFScore());

                        //comp = 0 si les F sont égaux
                        if (comp == 0)
                            return Float.compare(node0.HScore, node1.HScore);
                        else {
                            return comp;
                        }
                    }
                });




        start.GScore = 0f;
        start.HScore = heuristic.apply(start.node, goal.node);
        openSet.add(start);


        while (openSet.size() > 0) {

            current = openSet.poll();

            //System.out.println("CHOSEN : " + current.toString());

            closedSet.add(current);

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
            current.exploreNeighbors(closedSet, openSet, heuristic, goal);
        }

        log.finishLogging(path);

        throw new PathNotFoundException(startNode, goalNode);
    }

    @Override
    public void beginPathfinding(Node start, Node goal) throws PathNotFoundException {

        path = computeFullPath(start, goal);
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
}
