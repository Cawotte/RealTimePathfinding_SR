package uqac.graph.pathfinding;

import uqac.graph.Node;

public interface IRealTimePathfinding {

    //Classic Pathfinding
    Path computeFullPath(Node start, Node goal);

    //Real Time Pathfinding
    void beginPathfinding(Node start, Node goal);

    Node getNextStep();

    boolean hasFinished();

    Path getFinalPath();

    //Get Logs


}
