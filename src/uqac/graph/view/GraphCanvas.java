package uqac.graph.view;

import uqac.graph.*;
import uqac.graph.pathfinding.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/***
 * Classe qui se charge de lire et afficher le Graph dans la fênetre graphique.
 */
public class GraphCanvas extends JPanel {


    private final int nodeRadius = 8;

    private final Color colorPath = Color.red;
    private final Color colorGraph = Color.gray;

    private final Color colorVisited = Color.orange;
    private final Color colorStartNode = Color.blue;
    private final Color colorGoalNode = Color.orange;
    private final Color colorCurrentNode = Color.green;


    private WeightedGraph graph;

    //Windows size
    private int width;
    private int height;

    //Draw text
    private int xText = 10;
    private int yText = 10;

    //Graph bounds (in which it has to be drawn)
    private Vector2 minBounds;
    private Vector2 maxBounds;

    //Infos sur le graph
    private boolean displayVisited;
    private Collection<? extends INode> visited = null;
    private INode start = null;
    private INode goal = null;
    private INode current = null;
    private Path path;
    private String logText = "";
    private String cmpText = "";


    public GraphCanvas(WeightedGraph graph, int width, int height, int offsetWidth, int offsetHeight, boolean displayVisited) {
        this.graph = graph;
        this.width = width;
        this.height = height;
        this.displayVisited = displayVisited;

        this.minBounds = new Vector2(offsetWidth, offsetHeight);
        this.maxBounds = new Vector2(width - offsetWidth, height - offsetHeight);



    }

    @Override
    public void paintComponent(Graphics g) {

        paintBaseGraph(g);

        //Write algorithms logs
        writeText(g, logText, xText, yText);

        //Write parameters and algo comparisons
        writeText(g, cmpText, (int)this.maxBounds.x + xText, yText);

        if (displayVisited)
            paintVisited(g);


        if (path != null) {
            paintPath(g, path);
        }

        paintStartGoalCurrent(g);

    }

    public void updateWithAlgorithm(IRealTimePathfinding algorithm) {

        this.path = algorithm.getPathToDisplay();
        this.visited = algorithm.getVisited();
        this.current = algorithm.getCurrent();
        this.start = algorithm.getStart();
        this.goal = algorithm.getCurrent();
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public void setCmpText(String cmpText) {
        this.cmpText = cmpText;
    }

    private void paintBaseGraph(Graphics g) {

        //Draw all nodes
        for (Node node : graph.getNodes()) {
            drawCircle(g, node.position, nodeRadius, colorGraph);
        }

        drawLink(g);
    }

    private void writeText(Graphics g, String text, int xPos, int yPos) {

        Font myFont = new Font (Font.MONOSPACED, Font.PLAIN, 12);
        g.setFont (myFont);

        int x = xPos;
        int y = yPos;
        for (String line : text.split("\n")) {
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
        }

    }


    private void paintVisited(Graphics g) {
        if (visited == null)
            return;

        for (INode visitedNode : visited) {
            drawCircle(g, visitedNode.getPosition(), nodeRadius, colorVisited);
        }
    }

    private void paintStartGoalCurrent(Graphics g) {
        if (current != null) {
            drawCircle(g, current.getPosition(), nodeRadius, colorCurrentNode);
        }
        if (start != null) {
            drawCircle(g, start.getPosition(), nodeRadius, colorStartNode);
        }
        if (goal != null) {
            drawCircle(g, goal.getPosition(), nodeRadius, colorGoalNode);
        }
    }


    private void drawLink(Graphics g) {
        //Draw all links on top of them
        for (Node node : graph.getNodes()) {
            for (Node neighbor : node.getNeighbors()) {
                drawLine(g, node.position, neighbor.position, colorGraph);
            }
        }
    }

    private void paintPath(Graphics g, Path path) {

        INode previousNode = null;
        ArrayList<INode> nodes = path.getNodePath();
        for (INode node : nodes) {
            if (previousNode != null) {
                drawLine(g, previousNode.getPosition(), node.getPosition(), colorPath);
            }

            drawCircle(g, node.getPosition(), nodeRadius, colorPath);

            previousNode = node;
        }
    }

    private void drawLine(Graphics g, Vector2 a, Vector2 b, Color color) {
        Vector2 aGraph = posGraphToCanvas(a);
        Vector2 bGraph = posGraphToCanvas(b);

        g.setColor( color );
        g.drawLine( (int)aGraph.x, (int)aGraph.y, (int)bGraph.x, (int)bGraph.y);
    }

    private void drawCircle(Graphics g, Vector2 center, int radius, Color color) {
        Vector2 centerGraph = posGraphToCanvas(center);
        g.setColor(color);
        g.fillOval((int)centerGraph.x - radius / 2, (int)centerGraph.y - radius / 2,
                radius, radius);
    }

    private Vector2 posGraphToCanvas(Vector2 graphPos) {

        Vector2 canvasPos = new Vector2();
        canvasPos.x = lerp(minBounds.x, maxBounds.x, (graphPos.x - graph.getMinBound().x) / graph.getMaxBound().x);
        canvasPos.y = lerp(minBounds.y, maxBounds.y, (graphPos.y - graph.getMinBound().y) / graph.getMaxBound().y);

        return canvasPos;
    }

    private static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

}
