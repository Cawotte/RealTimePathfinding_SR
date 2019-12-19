package uqac.graph.pathfinding;

import uqac.graph.Node;
import uqac.graph.Vector2;


public class Heuristics {

    public static float euclidianDistance(Node a, Node b) {
        return Vector2.Distance(a.position, b.position);
    }

    public static float manhattanDistance(Node a, Node b) {
        return Math.abs(a.position.x - b.position.x) + Math.abs(a.position.y - b.position.y);
    }


}
