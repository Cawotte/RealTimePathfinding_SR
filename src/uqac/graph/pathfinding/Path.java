package uqac.graph.pathfinding;

import uqac.graph.Node;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.ArrayList;

public class Path {

    //region Fields
    private ArrayList<Node> path = new ArrayList<>();

    private boolean isCompleted = false;

    private float cost = 0f;

    //endregion

    public Path(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Path(ArrayList<Node> path, boolean isCompleted) {

        this.isCompleted = isCompleted;

        //Add path and compute cost
        for (int i = 0; i < path.size(); i++) {
            addNode(path.get(i));
        }

    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public void addNode(Node node) {
        if (!path.isEmpty()) {
            //Add the cost from the NEW node TO the PREVIOUS node.
            cost += path.get(0).getCostToNeighbor(node);
        }
        path.add(node);
    }

    public void addNodeAtBeginning(Node node) {
        if (!path.isEmpty()) {
            //Add the cost from the NEW node TO the PREVIOUS node.
            cost += node.getCostToNeighbor(path.get(0));
        }
        path.add(0, node);
    }

    public boolean removeLast() {

        if (!path.isEmpty()) {
            Node toRemove = getGoal(); //Goal is last, even if not completed

            //If there's currently at least two nodes
            if (path.size() > 1) {
                //Remove the cost from the previous one to the one to be removed.
                cost -= path.get(path.size() - 2).getCostToNeighbor(toRemove);
            }

            return path.remove(toRemove);
        }

        return false;
    }

    /**
     * Get the path suboptimality compared to the given optimal path in %.
     * An optimal path has a suboptimality of 0.
     * A path twice as long has a suboptimality of 200%.
     * @param optimalPath
     * @return
     */
    public float compareSuboptimality(Path optimalPath) {
        return ((getCost() / optimalPath.getCost() ) - 1f) * 100f;
    }

    //public void removeLast ?

    //region Getter/Setter

    //Getter
    boolean isCompleted() {
        return isCompleted;
    }

    public int getSize() {
        return path.size();
    }

    public float getCost() {
        return cost;
    }

    public Node getStart() {
        return path.get(0);
    }

    public Node getGoal() {
        return path.get(path.size() - 1);
    }

    public ArrayList<Node> getNodePath() {
        return path;
    }

    //Setter
    void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    //endregion
}
