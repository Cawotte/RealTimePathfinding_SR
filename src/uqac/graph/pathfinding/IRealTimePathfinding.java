package uqac.graph.pathfinding;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.Collection;

public interface IRealTimePathfinding {

    //Classic Pathfinding
    Path computeFullPath(Node start, Node goal) throws PathNotFoundException;

    //Real Time Pathfinding
    void beginPathfinding(Node start, Node goal) throws PathNotFoundException;

    Node getNextStep() throws PathNotFoundException;

    boolean hasFinished();

    Path getPathToDisplay();
    Path getPathWalked();

    Collection<? extends INode> getVisited();


    INode getStart();
    INode getGoal();
    INode getCurrent();


    //Get Logs
    LogPathfinding getLog();

}

