package uqac.graph.pathfinding;

import uqac.graph.Node;

public class PathNotFoundException extends Exception {

    public PathNotFoundException(Node start, Node goal) {
        super("Path not found between " + start.toString() + " and " + goal.toString() + " !");
    }
}
