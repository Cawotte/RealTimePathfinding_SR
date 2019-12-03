package uqac.graph.pathfinding;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.Collection;
import java.util.List;

public interface IRealTimePathfinding {

    //Classic Pathfinding
    Path computeFullPath(Node start, Node goal) throws PathNotFoundException;

    //Real Time Pathfinding
    void beginPathfinding(Node start, Node goal);

    Node getNextStep();

    boolean hasFinished();

    Path getPath();

    Collection<? extends INode> getVisited();

    //Get Logs
    LogPathfinding getLog();

}

