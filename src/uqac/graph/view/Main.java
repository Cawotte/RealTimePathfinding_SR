package uqac.graph.view;


import uqac.graph.*;
import uqac.graph.Vector2;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.BiFunction;

public class Main extends JFrame implements MouseListener, MouseMotionListener {

    // Define constants

    //GRAPH CONSTANT
    static final int NB_NODES = 100000;
    static final Vector2 RAND_OFFSET = new Vector2(0.3f, 0.3f); //coeff

    //CANVAS GRAPH CONSTANT
    static final int CANVAS_WIDTH  = 960;
    static final int CANVAS_HEIGHT = 600;
    static final Vector2 GRAPH_OFFSET = new Vector2(50, 50);

    //PATHFINDING TAB* PARAMETERS
    static final int MAX_STEP_EXPANSION = 100;
    static final int MAX_STEP_BACKTRACKING = 1000;

    private GraphCanvas canvas;

    private WeightedGraph graph;
    private PathCalculator pathfinder;

    private IRealTimePathfinding realTimePathfinding;
    private BiFunction<Node, Node, Float> heuristics = Heuristics::euclidianDistance;

    public Main() {

        //DATA

        //Calcule le ratio H*L requis pour avoir un graph équidistant en fonction du nombre total de nodes
        float aspectRatio = (float)CANVAS_WIDTH / (float)CANVAS_HEIGHT;

        int nbNodesY = (int)Math.sqrt(NB_NODES / aspectRatio);
        int nbNodesX = (int) (nbNodesY * aspectRatio);

        System.out.println("(" + nbNodesX + ", " + nbNodesY + ") = " + nbNodesX * nbNodesY);

        //Initialise le graph
       WeightedGraph graphInit = GraphFactory.generateGridGraph(
                nbNodesX, nbNodesY, RAND_OFFSET, true);
        this.graph = graphInit;

        this.graph = GraphFactory.generateGridGraph2(graphInit, 0.3);

        // SETUP PATHFINDIN
        int choix = 1;
        if (choix == 0) {
            realTimePathfinding = new AStar(heuristics);
        }
        else {
            realTimePathfinding = new TBAStar(heuristics,
                    MAX_STEP_EXPANSION,
                    MAX_STEP_BACKTRACKING);
        }

        this.pathfinder = new PathCalculator(graph, realTimePathfinding);
        this.pathfinder.notifyObserver = this::updateAndRepaintGraph;

        //Start generating random pathfinding
        pathfinder.scheduleNextTimer();

        /// SETUP GRAPH CANVAS

        canvas = new GraphCanvas(graph, CANVAS_WIDTH, CANVAS_HEIGHT, (int)GRAPH_OFFSET.x, (int)GRAPH_OFFSET.y);


        /// SETUP WINDOWS

        // Construct the drawing canvas
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));


        // Set the Test.Drawing JPanel as the JFrame's content-pane
        Container cp = getContentPane();
        cp.add(canvas);
        // or "setContentPane(canvas);"

        addMouseListener( this );
        addMouseMotionListener( this );

        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Handle the CLOSE button
        pack();              // Either pack() the components; or setSize()
        setTitle("Real Time Pathfinding");  // "super" JFrame sets the title
        setVisible(true);    // "super" JFrame show
    }

    private void updateAndRepaintGraph() {

        canvas.setPath(pathfinder.getPathfindingAlgorithm().getPath());
        canvas.setVisited(pathfinder.getPathfindingAlgorithm().getVisited());
        canvas.setStart(pathfinder.getStart());
        canvas.setGoal(pathfinder.getGoal());
        canvas.setCurrent(pathfinder.getCurrent());
        this.repaint();
    }

    public static void main(String[] args) {

        // Run the GUI codes on the Event-Dispatching thread for thread safety

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main(); // Let the constructor do the job
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        canvas.setPosPointer(mouseEvent.getX(), mouseEvent.getY());
        this.repaint();
    }
}
