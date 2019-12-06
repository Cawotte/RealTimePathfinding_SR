package uqac.graph;

public interface INode {

    Vector2 getPosition();

    float getCostToNeighbor(INode neighbor);
}
