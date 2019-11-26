package uqac.graph.pathfinding;

import uqac.graph.*;

import java.util.Random;

public class GraphFactory {


    public static WeightedGraph generateGridGraph(int width, int height, int distanceBetweenNodes,
        Vector2 graphOffset, Vector2 randOffset) {

        //Create a 2D array of Node, then convert it to an actual Graph object.

        int nbNodeWidth = (width - (int)graphOffset.x) / distanceBetweenNodes;
        int nbNodeHeight = (height - (int)graphOffset.y) / distanceBetweenNodes;

        int offsetNodeX = (int)(distanceBetweenNodes * randOffset.x);
        int offsetNodeY = (int)(distanceBetweenNodes * randOffset.y);

        //temp array for better gen
        Node[][] arrayGraph = new Node[nbNodeWidth][nbNodeHeight];

        //TODO : SEED THAT RANDOM
        Random rand = new Random();

        for (int i = 0; i < nbNodeWidth; i++) {
            for (int j = 0; j < nbNodeHeight; j++) {

                int x = (int)graphOffset.x + i * distanceBetweenNodes;
                int y = (int)graphOffset.y + j * distanceBetweenNodes;

                //apply offset, range [-offset / 2, +offset / 2]
                if (offsetNodeX != 0)
                    x += rand.nextInt(offsetNodeX) - offsetNodeX / 2;
                if (offsetNodeY != 0)
                    y += rand.nextInt(offsetNodeY) - offsetNodeY / 2;

                arrayGraph[i][j] = new Node(x, y);
            }
        }

        WeightedGraph gridGraph = new WeightedGraph();

        //set neighbors
        for (int i = 0; i < nbNodeWidth; i++) {
            for (int j = 0; j < nbNodeHeight; j++) {


                if (i > 0) {
                    arrayGraph[i][j].addNeighbor(arrayGraph[i - 1][j]);
                }
                if (j > 0) {
                    arrayGraph[i][j].addNeighbor(arrayGraph[i][j - 1]);
                }
                if (i < nbNodeWidth - 1) {
                    arrayGraph[i][j].addNeighbor(arrayGraph[i + 1][j]);
                }
                if (j < nbNodeHeight - 1) {
                    arrayGraph[i][j].addNeighbor(arrayGraph[i][j + 1]);
                }

                //Add the node to the Weigted Graph
                gridGraph.addNode(arrayGraph[i][j]);
            }
        }

        return gridGraph;
    }
}
