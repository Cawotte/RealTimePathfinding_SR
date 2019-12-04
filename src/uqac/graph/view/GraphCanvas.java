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

    private int width;
    private int height;
    private Vector2 min;
    private Vector2 max;

    private Collection<? extends INode> visited = null;

    private Circle pointer = new Circle(0, 0, 5, Color.red);

    private Path path;

    public GraphCanvas(WeightedGraph graph, int width, int height) {
        this.graph = graph;
        this.width = width;
        this.height = height;
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
        g.setColor( color );
        g.drawLine( (int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }

    private Node drawLineToClosestNode(Graphics g) {


        Node closestNode = graph.getClosestNode(pointer.center);
        float distance = Vector2.Distance(pointer.center, closestNode.position);
        drawLine(g, pointer.center, closestNode.position, colorShortDistance);
        return closestNode;
    }

    private void drawCircle(Graphics g, Vector2 center, int radius, Color color) {
        g.setColor(color);
        g.fillOval((int)center.x - radius / 2, (int)center.y - radius / 2,
                radius, radius);
    }


    public void setPosPointer(int x, int y) {
        pointer.center.x = x;
        pointer.center.y = y;
    }

}
