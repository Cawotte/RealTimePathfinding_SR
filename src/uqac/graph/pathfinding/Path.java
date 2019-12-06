package uqac.graph.pathfinding;

import uqac.graph.INode;

import java.util.ArrayList;
import java.util.Iterator;

public class Path<T extends INode> {

    //region Fields
    private ArrayList<T> path = new ArrayList<>();

    private boolean isCompleted = false;

    private float cost = 0f;

    private PathIterator<T> iterator;
    //endregion

    public Path() {
        this.isCompleted = false;
        this.iterator = new PathIterator<>(this);

    }
    public Path(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Path(ArrayList<T> path, boolean isCompleted) {

        this.isCompleted = isCompleted;

        //Add path and compute cost
        for (int i = 0; i < path.size(); i++) {
            addNode(path.get(i));
        }

    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public void addNode(T node) {
        if (!path.isEmpty()) {
            //Add the cost from the NEW node TO the PREVIOUS node.
            cost += path.get(0).getCostToNeighbor(node);
        }
        path.add(node);
    }

    public void addNodeAtBeginning(T node) {
        if (!path.isEmpty()) {
            //Add the cost from the NEW node TO the PREVIOUS node.

            cost += node.getCostToNeighbor(path.get(0));
        }
        path.add(0, node);
        iterator.increaseIndex();
    }

    public boolean removeLast() {

        if (!path.isEmpty()) {
            T toRemove = getGoal(); //Goal is last, even if not completed

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

    @Override
    public String toString() {
        String str = "";
        int i = 0;
        for (T node : path) {
            str += i + " : " + node.toString() + "\n";
            i++;
        }
        return str;
    }

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

    public T getStart() {
        return path.get(0);
    }

    public T getGoal() {
        return path.get(path.size() - 1);
    }

    public PathIterator<T> getIterator() {
        return iterator;
    }

    public int indexOf(T node) {
        return path.indexOf(node);
    }

    public ArrayList<T> getNodePath() {
        return path;
    }

    //Setter
    void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    //endregion

    class PathIterator<T extends INode> implements Iterator<T> {

        private int index;
        private T current;
        private Path<T> path;

        public PathIterator(Path<T> path) {
            this.path = path;
            this.current = null;
            this.index = 0;
        }

        public T getCurrent() {
            return path.getNodePath().get(index);
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void increaseIndex() {
            this.index++;
        }

        @Override
        public boolean hasNext() {
            return index < path.getSize();
        }

        @Override
        public T next() {
            return path.getNodePath().get(++index);
        }
    }
}
