package uqac.graph.pathfinding;

import uqac.graph.*;

import java.util.Random;

public class GraphFactory {


    public static WeightedGraph generateGridGraph(int width, int height, int distanceBetweenNodes,
        float offsetX, float offsetY) {

        //Create a 2D array of Node, then convert it to an actual Graph object.

        int nbNodeWidth = width / distanceBetweenNodes;
        int nbNodeHeight = height / distanceBetweenNodes;

        int offsetNodeX = (int)(distanceBetweenNodes * offsetX);
        int offsetNodeY = (int)(distanceBetweenNodes * offsetY);

        //temp array for better gen
        Node[][] arrayGraph = new Node[nbNodeWidth][nbNodeHeight];

        //TODO : SEED THAT RANDOM
        Random rand = new Random();

        for (int i = 0; i < nbNodeWidth; i++) {
            for (int j = 0; j < nbNodeHeight; j++) {

                int x = i * distanceBetweenNodes;
                int y = j * distanceBetweenNodes;

                //apply offset, range [-offset / 2, +offset / 2]
                x += rand.nextInt(offsetNodeX) - offsetNodeX / 2;
                y += rand.nextInt(offsetNodeY) - offsetNodeY / 2;

                arrayGraph[i][j] = new Node(x, y);
            }
        }

        WeightedGraph gridGraph = new WeightedGraph();

        //set neighbors
        for (int i = 0; i < nbNodeWidth; i++) {
            for (int j = 0; j < nbNodeHeight; j++) {

                //Add the node to the Weigted Graph
                gridGraph.addNode(arrayGraph[i][j]);

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
            }
        }

        return gridGraph;
    }
}
