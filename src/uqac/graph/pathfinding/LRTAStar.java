package uqac.graph.pathfinding;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;

public class LRTAStar implements IRealTimePathfinding{

    private ArrayList<NodeLRTA> updatedNodes;

    private int lookAhead;

    private NodeLRTA currentAgentNode;
    private NodeLRTA start;
    private NodeLRTA goal;

    private BiFunction<Node, Node, Float> heuristic;

    private Path<Node> pathWalked;

    private LogPathfinding log = new LogPathfinding();

    public LRTAStar(BiFunction<Node, Node, Float> heuristic, int lookAhead) {
        this.lookAhead = lookAhead;
        this.heuristic = heuristic;
    }

    @Override
    public Path computeFullPath(Node start, Node goal) throws PathNotFoundException {
        beginPathfinding(start, goal);

        while (!hasFinished()) {
            getNextStep();
        }

        return pathWalked;
    }

    @Override
    public void beginPathfinding(Node start, Node goal) throws PathNotFoundException {

        updatedNodes = new ArrayList<>();


        this.start = new NodeLRTA(start);
        this.goal = new NodeLRTA(goal);
        this.currentAgentNode = this.start;

        this.pathWalked = new Path<>();
        this.pathWalked.addNodeAtBeginning(start);

        log.startLogging();
    }

    @Override
    public Node getNextStep() {

        ArrayList<NodeLRTA> frontier = new ArrayList<>();

        NodeLRTA bestChoice = null;

        frontier.add(currentAgentNode);


        //reset GScore to extend from current
        currentAgentNode.GScore = 0f;
        currentAgentNode.depth = 0;

        do {

            NodeLRTA current = frontier.remove(0);

            if (current.equals(goal))  {
                bestChoice = current;
                break; //There can't be anything better than the goal duh.
            }
            else if (current.depth == lookAhead) {

                //Is it new best ?
                if (bestChoice == null) {
                    bestChoice = current;
                }
                else if (current.getFScore() < bestChoice.getFScore()) {
                    bestChoice = current;
                }
                else if (current.getFScore() == bestChoice.getFScore() && current.GScore > bestChoice.GScore) {
                    bestChoice = current;
                }
            }


            for (Node neigh : current.node.getNeighbors()) {

                //Initialize all neighbors as NodeAStars.

                NodeLRTA neighbor = new NodeLRTA(neigh);

                //We skip neighbors already computed
                if (frontier.contains(neighbor)) {
                    continue;
                }

                int index = updatedNodes.indexOf(neighbor);

                //That node already exists
                if (index >= 0) {
                    System.out.println("Already exists!");
                    neighbor = updatedNodes.get(index);
                    neighbor.GScore = current.GScore + current.node.getCostToNeighbor(neighbor.node);
                    neighbor.parent = current;
                    neighbor.depth = current.depth + 1;
                }
                //initialize node
                else {
                    neighbor.GScore = current.GScore + current.node.getCostToNeighbor(neighbor.node);
                    neighbor.HScore = heuristic.apply(neighbor.node, goal.node);
                    neighbor.parent = current;
                    neighbor.depth = current.depth + 1;
                }


                //If their G is less than GMax, add them to the frontier.
                if (neighbor.depth <= lookAhead) {
                    frontier.add(neighbor);
                }
            }

        } while( !frontier.isEmpty() );

        //Update current node with new known more precise heuristic
        currentAgentNode.HScore = bestChoice.getFScore();


        //We found the next step to take to move toward that best choice.
        while (!currentAgentNode.equals(bestChoice.parent)) {
            bestChoice = bestChoice.parent;
        }

        //next step
        currentAgentNode = bestChoice;
        pathWalked.addNodeAtBeginning(bestChoice.node);

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
        return currentAgentNode.equals(goal);
    }

    @Override
    public Path getPathToDisplay() {
        return pathWalked;
    }

    @Override
    public Path getPathWalked() {
        return pathWalked;
    }

    @Override
    public Collection<? extends INode> getVisited() {
        return updatedNodes;
    }

    @Override
    public LogPathfinding getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "LRTA* Look-ahead " + lookAhead;
    }

    class NodeLRTA extends NodeAStar {

        int depth = 0;
        NodeLRTA parent;

        NodeLRTA(Node node) {
            super(node);
        }


        @Override
        public String toString() {

            DecimalFormat df = new DecimalFormat("0.00");

            String str = "";
            str += "(" + df.format(getPosition().x) + ", " + df.format(getPosition().y) + ")";
            //str += ", (" + GScore + ", " + HScore + ", " + getFScore() + ")";
            return str;
        }

    }
}
