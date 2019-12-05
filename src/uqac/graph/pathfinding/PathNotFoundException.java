package uqac.graph.pathfinding;

import uqac.graph.Node;

public class PathNotFoundException extends Exception {

    public PathNotFoundException(Node start, Node goal) {
        super("Path not found between Node(" + start.getIdX() + ", " + start.getIdY() +
                ") and Node(" + goal.getIdX() + ", " + goal.getIdY() + ") !");
    }
}
