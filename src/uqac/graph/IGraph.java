package uqac.graph;

import java.util.ArrayList;
import java.util.Collection;

/***
 * Interface représentant un Graph. Tout les uqac.graph doivent l'implémenter.
 */
public interface IGraph {

    Collection<Node> getNodes();

    int getSize();

    boolean contains(Node node);
}
