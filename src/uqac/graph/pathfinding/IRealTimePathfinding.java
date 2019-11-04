package uqac.graph.pathfinding;

import uqac.graph.Node;

public interface IRealTimePathfinding {

    void startPathfinding(Node start, Node goal);

    Node getNextStep();

    boolean hasFinished();

    Path getFinalPath();


}
