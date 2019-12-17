package uqac.graph.pathfinding;

import uqac.graph.*;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.*;
import java.util.function.BiFunction;

public class AStar implements IRealTimePathfinding {

    //TODO : Improve algo with sorting by ascending F
    //TODO : Add Logs

    private BiFunction<Node, Node, Float> heuristic;

    private Path<Node> path = new Path<>();
    private Iterator<Node> iterator = null;

    private LogPathfinding log = new LogPathfinding();

    private NodeAStar start = null;
    private NodeAStar goal = null;
    private INode current = null;

    HashSet<NodeAStar> closedSet = new HashSet<>();

    public AStar(BiFunction<Node, Node, Float> heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Path computeFullPath(Node startNode, Node goalNode) throws PathNotFoundException
    {

        start = new NodeAStar(startNode);
        goal = new NodeAStar(goalNode);
        this.current = start;
        NodeAStar current;

        //initialize new path
        this.path = new Path<>();

        log.startLogging(toString(), getPathWalked());

        if (start.equals(goal)) {
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

                        //lowest f has priority
                        int comp = Float.compare(node0.getFScore(), node1.getFScore());

                        //lowest G has priority
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
            current = iterator.next();
            return (Node)current;
        }
        return null;
    }

    @Override
    public boolean hasFinished() {
        return !iterator.hasNext();
    }

    @Override
    public Path getPathToDisplay() {
        return path;
    }

    @Override
    public Path getPathWalked() {
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

    @Override
    public INode getStart() {
        return start;
    }

    @Override
    public INode getGoal() {
        return goal;
    }

    @Override
    public INode getCurrent() {
        return current;
    }

    @Override
    public String toString() {
        return "A*";
    }
}
