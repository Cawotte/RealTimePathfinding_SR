package uqac.graph.pathfinding;

import javafx.css.converter.DeriveColorConverter;
import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.Vector2;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;

public class LRTAStar implements IRealTimePathfinding{

    private ArrayList<NodeLRTA> updatedNodes;

    private int lookAhead;

    private NodeLRTA currentAgentNode = null;
    private NodeLRTA start = null;
    private NodeLRTA goal = null;

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

        log.startLogging(toString(), this.pathWalked);
    }

    @Override
    public Node getNextStep() {

        PriorityQueue<NodeLRTA> frontier = new PriorityQueue<NodeLRTA>(
                new Comparator<NodeLRTA>() {
                    @Override
                    public int compare(NodeLRTA node0, NodeLRTA node1) {

                        //lowest G Has priority
                        int comp = Float.compare(node0.GScore, node1.GScore);

                        //On equal G, take lowest depth
                        if (comp == 0)
                             return Integer.compare(node0.depth, node1.depth);
                        else {
                            return comp;
                        }
                    }
                });
        ArrayList<Vector2> frontierPos = new ArrayList<>();

        NodeLRTA bestChoice = null;

        frontier.add(currentAgentNode);
        frontierPos.add(currentAgentNode.getPosition());


        //reset GScore to extend from current
        currentAgentNode.GScore = 0f;
        currentAgentNode.depth = 0;


        do {

            NodeLRTA current = frontier.poll();


            if (current.equals(goal))  {
                bestChoice = current;
                break; //There can't be anything better than the goal duh.
            }
            //Only look for furthest depth
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


                //We skip neighbors already computed
                if (frontierPos.contains(neigh.position)) {
                    continue;
                }

                NodeLRTA neighbor = new NodeLRTA(neigh);

                int index = updatedNodes.indexOf(neighbor);

                //That node already exists
                if (index >= 0) {
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
                    frontierPos.add(neighbor.node.position);
                }

            }

        } while( !frontier.isEmpty() );

        //Update current node with new known more precise heuristic
        currentAgentNode.HScore = bestChoice.getFScore();
        if (!updatedNodes.contains(currentAgentNode)) {
            updatedNodes.add(currentAgentNode);
        }

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
        return "LRTA* Look-ahead " + lookAhead;
    }

    class NodeLRTA extends NodeAStar {

        int depth = 0;
        NodeLRTA parent;

        NodeLRTA(Node node) {
            super(node);
        }



    }
}
