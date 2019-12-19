package uqac.graph;

import uqac.graph.utils.Vector2;

public interface INode {

    Vector2 getPosition();

    float getCostToNeighbor(INode neighbor);
}
