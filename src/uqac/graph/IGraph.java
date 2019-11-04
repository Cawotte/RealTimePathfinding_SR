package uqac.graph;

import java.util.ArrayList;

/***
 * Interface représentant un Graph. Tout les uqac.graph doivent l'implémenter.
 */
public interface IGraph {

    ArrayList<Node> getNodes();

    int getSize();

    boolean contains(Node node);
}
