package uqac.graph.pathfinding;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.*;
import java.util.function.BiFunction;

public class TBAStar implements IRealTimePathfinding {

    private NodeAStar start;
    private NodeAStar goal;

    private NodeAStar currentAgentNode;
    private NodeAStar currentBestNode;
    private NodeAStar lastAgentNode; //used for fringe case scenarios

    private int nbStepExpansion;
    private int nbStepBacktracking;

    private Path<NodeAStar> pathNew;
    private Path<NodeAStar> pathFollow;
    private Path<Node> pathWalked;

    private boolean solutionFound = false;
    private boolean solutionFoundAndTraced = false;
    private boolean doneTrace = true;


    private BiFunction<Node, Node, Float> heuristic;
    private LogPathfinding log = new LogPathfinding();

    HashSet<NodeAStar> closedSet = new HashSet<>();
    PriorityQueue<NodeAStar> openSet;

    public TBAStar(BiFunction<Node, Node, Float> heuristic, int nbStepExpansion, int nbStepBacktracking)
    {
        this.heuristic = heuristic;
        this.nbStepBacktracking = nbStepBacktracking;
        this.nbStepExpansion = nbStepExpansion;
    }

    @Override
    public Path computeFullPath(Node start, Node goal) throws PathNotFoundException {
        return null;
    }

    @Override
    public void beginPathfinding(Node start, Node goal) {

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
        this.currentAgentNode = this.start;

        this.pathFollow = new Path<>();
        this.pathNew = new Path<>();
        this.pathWalked = new Path<>();
        this.pathWalked.addNodeAtBeginning(start);


        openSet.add(this.start);

        log.startLogging(toString(), this.pathWalked);

    }


    @Override
    public Node getNextStep() throws PathNotFoundException {

        planningPhase();

        executionPhase();

        if (hasFinished()) {
            log.finishLogging(pathWalked);
        }
        else {
            log.addStep();
        }


        return currentAgentNode.node;
    }

    @Override
    public boolean hasFinished() {
        return goal.equals(currentAgentNode);
    }


    @Override
    public Path getPathToDisplay() {
        return pathFollow;
    }

    @Override
    public Path getPathWalked() {
        return pathWalked;
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
        return currentAgentNode;
    }

    @Override
    public LogPathfinding getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "TBA* Ne-" + nbStepExpansion + " Nt-" + nbStepBacktracking;
    }

    private void planningPhase() throws PathNotFoundException {

        //While the path to goal has not been discovered
        if (!solutionFound) {
            solutionFound = stateExpansion(nbStepExpansion);
        }
        //Backtracking phase
        if (!solutionFoundAndTraced) {

            if (doneTrace) {
                pathNew = new Path<>();
                pathNew.addNode(currentBestNode);
            }

            doneTrace = traceback(pathNew, nbStepBacktracking);

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

            log.finishLogging(pathWalked);
            throw new PathNotFoundException(start.node, goal.node);
        }

        //No definite path found yet, end of state expansion phase !

        return false;
    }


    private boolean traceback(Path pathNew, int nbStepBacktracking) {

        int nbStep = 0;
        NodeAStar current = (NodeAStar)pathNew.getStart();

        while (!pathHasFinishedBacktracking(current) && nbStepBacktracking > nbStep) {

            pathNew.addNodeAtBeginning(current.parent);
            current = current.parent;
            nbStep++;
        }


        return pathHasFinishedBacktracking(current);
    }

    private void executionPhase() {


        //Execution Phase
        int indexAgent = pathFollow.indexOf(currentAgentNode);

        //If agent is on path
        if (indexAgent >= 0) {

            //If the agent is not on the current path node
            if (!pathFollow.getIterator().getCurrent().equals(currentAgentNode)) {

                //Set agent to correct index (happen when the agent cross a new path midway)
                pathFollow.getIterator().setIndex(indexAgent);
            }

            currentAgentNode = pathFollow.getIterator().next();
        }
        else {
            //Backtrack until reaching current path
            if (!currentAgentNode.equals(start)) {
                //step back
                currentAgentNode = currentAgentNode.parent;
            }
            else {
                currentAgentNode = lastAgentNode;
            }
        }

        lastAgentNode = currentAgentNode;

        //Register every steps done
        pathWalked.addNodeAtBeginning(currentAgentNode.node);


    }

    private boolean pathHasFinishedBacktracking(NodeAStar current) {
        return current.equals(currentAgentNode) || current.equals(start);
    }


}
