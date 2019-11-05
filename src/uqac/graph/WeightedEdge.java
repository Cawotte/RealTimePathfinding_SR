package uqac.graph;

public class WeightedEdge {

    private float cost;

    private Node source;
    private Node destination;

    public WeightedEdge(Node leftNode, Node rightNode, float cost) {
        this.source = leftNode;
        this.destination = rightNode;
        this.cost = cost;
    }

    public float getCost() {
        return cost;
    }

    public Node getOtherNode(Node node) {
        if (source.equals(node)) {
            return destination;
        }
        return destination;
    }
}
