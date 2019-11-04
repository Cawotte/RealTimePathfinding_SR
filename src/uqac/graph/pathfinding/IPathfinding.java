package uqac.graph.pathfinding;

import uqac.graph.*;

public interface IPathfinding {

    Path getPath(Node start, Node goal) throws PathNotFoundException;


}
