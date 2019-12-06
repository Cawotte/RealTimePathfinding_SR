package uqac.graph.pathfinding;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.*;
import java.util.function.BiFunction;

public class TBAStar implements IRealTimePathfinding {

    private NodeAStar start;
    private NodeAStar goal;

    private NodeAStar currentAgentNode;
    private NodeAStar currentBestNode;

    private int nbStepExpansion;
    private int nbStepBacktracking;

    private Path<NodeAStar> pathNew;
    private Path<NodeAStar> pathFollow;

    boolean solutionFound = false;
    boolean solutionFoundAndTraced = false;
    boolean doneTrace = true;


    private BiFunction<Node, Node, Float> heuristic;
    private LogPathfinding log = new LogPathfinding();

    HashSet<NodeAStar> closedSet = new HashSet<>();
    PriorityQueue<NodeAStar> openSet;

    @Override
    public Path computeFullPath(Node start, Node goal) throws PathNotFoundException {
        return null;
    }

    @Override
    public void beginPathfinding(Node start, Node goal) {


        log.startLogging();

        this.solutionFound = false;
        this.solutionFoundAndTraced = false;
        this.doneTrace = true;

        closedSet = new HashSet<>();
        openSet = new PriorityQueue<NodeAStar>(
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
            }
        );

        this.start = new NodeAStar(start);
        this.goal = new NodeAStar(goal);


        openSet.add(this.start);

    }


    @Override
    public Node getNextStep() {

        return null;

    }

    @Override
    public boolean hasFinished() {
        return false;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public Collection<? extends INode> getVisited() {
        return null;
    }

    @Override
    public LogPathfinding getLog() {
        return null;
    }

    private void planningPhase() throws PathNotFoundException {

        //While the path to goal has not been discovered
        if (!solutionFound) {
            solutionFound = stateExpansion(nbStepExpansion);
        }
        //Backtracking phase
        else if (!solutionFoundAndTraced) {

            if (doneTrace) {
                pathNew = new Path<>();
                pathNew.addNode(currentBestNode);
            }

            doneTrace = traceback(pathNew, currentAgentNode, nbStepBacktracking);

            if (doneTrace) {
                pathFollow = pathNew;
                if (pathFollow.getGoal().equals(goal)) {
                    solutionFoundAndTraced = true;
                }
            }
        }

    }

    private boolean stateExpansion(int nbStepExpansion) throws PathNotFoundException {

        int nbCurrentStep = 0;

        while (openSet.size() > 0 && nbStepExpansion > nbCurrentStep) {

            currentBestNode = openSet.poll();

            //System.out.println("CHOSEN : " + current.toString());

            closedSet.add(currentBestNode);

            //Goal is found, time to build the final path!
            if (currentBestNode.equals(goal)) {

                return true;
            }

            //get neighbors
            currentBestNode.exploreNeighbors(closedSet, openSet, heuristic, goal);

            nbCurrentStep++;
        }

        if (openSet.size() == 0) {

            log.finishLogging(pathFollow);
            throw new PathNotFoundException(start.node, goal.node);
        }

        //No definite path found yet, end of state expansion phase !

        return false;
    }


    private boolean traceback(Path pathNew, NodeAStar currentAgentNode, int nbStepBacktracking) {

        int nbStep = 0;
        NodeAStar current = (NodeAStar)pathNew.getGoal();

        while (!pathHasFinishedBacktracking(pathNew) && nbStepBacktracking > nbStep) {

            pathNew.addNode(current);

            nbStep++;
        }


        return pathHasFinishedBacktracking(pathNew);
    }

    private void executionPhase() {

        //Execution Phase

    }

    private boolean pathHasFinishedBacktracking(Path pathNew) {
        return pathNew.getGoal().equals(currentAgentNode.node) || pathNew.getGoal().equals(start.node);
    }


}
