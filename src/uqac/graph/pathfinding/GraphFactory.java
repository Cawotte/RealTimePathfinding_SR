package uqac.graph.pathfinding;

import uqac.graph.*;

import java.util.Random;

public class GraphFactory {


    public static WeightedGraph generateGridGraph(int nbNodeWidth, int nbNodeHeight,
                                                  Vector2 randOffset, boolean hasDiagonal) {

        //int nbNodeWide, int nbNodeWidth, int randOffset
        //Create a 2D array of Node, then convert it to an actual Graph object.

        //temp array for better gen
        Node[][] arrayGraph = new Node[nbNodeWidth][nbNodeHeight];

        //TODO : SEED THAT RANDOM
        Random rand = new Random();

        for (int i = 0; i < nbNodeWidth; i++) {
            for (int j = 0; j < nbNodeHeight; j++) {

                float x = i;
                float y = j;

                //apply offset, range [-offset / 2, +offset / 2]
                x += rand.nextFloat() * randOffset.x - randOffset.x / 2;
                y += rand.nextFloat() * randOffset.y - randOffset.y / 2;

                int[] idNode = {i,j};
                arrayGraph[i][j] = new Node(x, y, idNode);
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

                if (hasDiagonal) {
                    // add diagonale edges !!
                    if (i > 0 && j > 0) {
                        arrayGraph[i][j].addNeighbor(arrayGraph[i - 1][j - 1]);
                    }
                    if (i < nbNodeWidth - 1 && j > 0) {
                        arrayGraph[i][j].addNeighbor(arrayGraph[i + 1][j - 1]);
                    }
                    if (i > 0 && j < nbNodeHeight - 1) {
                        arrayGraph[i][j].addNeighbor(arrayGraph[i - 1][j + 1]);
                    }
                    if (i < nbNodeWidth - 1 && j < nbNodeHeight - 1) {
                        arrayGraph[i][j].addNeighbor(arrayGraph[i + 1][j + 1]);
                    }
                }

                //Add the node to the Weigted Graph
                gridGraph.addNode(arrayGraph[i][j]);
            }
        }

        return gridGraph;
    }


    /**
     * Delete edges from an initial graph with a probability of coeff
     * No isolated nodes
     * @param graphInit WeightedGraph
     * @param coeff double, probability
     * @return
     */
    public static WeightedGraph generateGridGraph2(WeightedGraph graphInit, double coeff){
        for(Node nodeGraph : graphInit.getNodes()){
            for(Node nodeNeig : nodeGraph.getNeighbors()){
                if(Math.random() < coeff && nodeGraph.getNeighbors().size() > 1){
                    nodeGraph.deleteNeighbor(nodeNeig);
                    //remove in both directions
                    nodeNeig.deleteNeighbor(nodeGraph);
                }
            }
        }

        return graphInit;
    }

    public void addDiagonalEdges(){

    }
}
