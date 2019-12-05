package uqac.graph.view;

import uqac.graph.*;
import uqac.graph.pathfinding.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class GraphCanvas extends JPanel {

    public boolean drawLinks = false;

    private final int nodeRadius = 8;

    private final Color colorPath = Color.red;
    private final Color colorGraph = Color.gray;

    private final Color colorVisited = Color.orange;
    private final Color colorStartNode = Color.blue;

    private final Color colorShortDistance = Color.blue;

    private WeightedGraph graph;

    //Windows size
    private int width;
    private int height;

    //Graph bounds (in which it has to be drawn)
    private Vector2 minBounds;
    private Vector2 maxBounds;

    //Visited nodes of the graph
    private Collection<? extends INode> visited = null;

    private Circle pointer = new Circle(0, 0, 5, Color.red);

    private Path path;

    public GraphCanvas(WeightedGraph graph, int width, int height, int offsetWidth, int offsetHeight) {
        this.graph = graph;
        this.width = width;
        this.height = height;

        this.minBounds = new Vector2(offsetWidth, offsetHeight);
        this.maxBounds = new Vector2(width - offsetWidth, height - offsetHeight);


    }

    @Override
    public void paintComponent(Graphics g) {

        paintBaseGraph(g);
        pointer.draw(g);

        paintVisited(g);

        //drawLineToClosestNode(g);

        if (path != null) {
            paintPath(g, path);
        }

    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setVisited(Collection<? extends INode> visited) {
        this.visited = visited;
    }

    private void paintBaseGraph(Graphics g) {

        //Draw all nodes
        for (Node node : graph.getNodes()) {
            drawCircle(g, node.position, nodeRadius, colorGraph);
        }

        if (drawLinks)
            drawLink(g);
    }

    private void paintVisited(Graphics g) {
        if (visited == null)
            return;

        for (INode visitedNode : visited) {
            drawCircle(g, visitedNode.getPosition(), nodeRadius, colorVisited);
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

        Node previousNode = null;
        for (Node node : path.getNodePath()) {
            if (previousNode != null) {
                drawLine(g, previousNode.position, node.position, colorPath);
            }

            //if not first node
            if (previousNode != null)  {
                drawCircle(g, node.position, nodeRadius, colorPath);
            }
            else {
                drawCircle(g, node.position, nodeRadius * 2, colorStartNode);
            }

            previousNode = node;
        }
    }

    private void drawLine(Graphics g, Vector2 a, Vector2 b, Color color) {
        Vector2 aGraph = posGraphToCanvas(a);
        Vector2 bGraph = posGraphToCanvas(b);

        g.setColor( color );
        g.drawLine( (int)aGraph.x, (int)aGraph.y, (int)bGraph.x, (int)bGraph.y);
    }

    /*
    private Node drawLineToClosestNode(Graphics g) {


        Node closestNode = graph.getClosestNode(pointer.center);
        float distance = Vector2.Distance(pointer.center, closestNode.position);
        drawLine(g, pointer.center, closestNode.position, colorShortDistance);
        return closestNode;
    } */

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


    public void setPosPointer(int x, int y) {
        pointer.center.x = x;
        pointer.center.y = y;
    }

}
