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
import java.util.ArrayList;
import java.util.function.BiFunction;

public class Main extends JFrame implements MouseListener, MouseMotionListener {

    // Define constants

    //GRAPH CONSTANT
    static final int NB_NODES = 1000;
    static final Vector2 RAND_OFFSET = new Vector2(0.3f, 0.3f);
    static final boolean HAS_DIAGONALS = true;
    static final float PROBABILITY_DISABLE_EDGE = 0.4f;

    //CANVAS GRAPH CONSTANT

    static final boolean GRAPHICS_ENABLED = true;

    static final int CANVAS_WIDTH  = 960;
    static final int CANVAS_HEIGHT = 600;
    static final Vector2 GRAPH_OFFSET = new Vector2(50, 50);

    //Afficher les noeuds visités? (Jaune). Peut entrainer des erreurs d'accès en
    //true sur des très gros graph.
    static final boolean DISPLAY_VISITED = true;

    //PATHFINDING CHOICE
    // 0 = A*, 1 = TAB*
    static final int PATHFINDING_CHOICE = 3;

    //Nombre de milliseconde minimal par étape de l'algorithme.
    //Mettre 0 pour la performance,
    //et plus entre 10 et 100 pour voir le déroulement de l'algo avec l'affichage
    static final int MIN_LENGHT_STEP = 100;

    //PATHFINDING TAB* PARAMETERS
    static final int MAX_STEP_EXPANSION = 1000;
    static final int MAX_STEP_BACKTRACKING = 10000;

    //LRTA* PARAMETERS
    static final int LOOKAHEAD = 10;

    private GraphCanvas canvas;

    private WeightedGraph graph;
    private PathGenerator pathfindingGenerator;

    private ArrayList<IRealTimePathfinding> pathAlgorithms = new ArrayList<>();
    private BiFunction<Node, Node, Float> heuristics = Heuristics::euclidianDistance;

    public Main() {

        //DATA

        //Calcule le ratio H*L requis pour avoir un graph équidistant en fonction du nombre total de nodes
        float aspectRatio = (float)CANVAS_WIDTH / (float)CANVAS_HEIGHT;

        int nbNodesY = (int)Math.sqrt(NB_NODES / aspectRatio);
        int nbNodesX = (int) (nbNodesY * aspectRatio);

        System.out.println("Graph Size : (" + nbNodesX + ", " + nbNodesY + ") = " + nbNodesX * nbNodesY + " nodes.");

        //Initialise le graph
       WeightedGraph graphInit = GraphFactory.generateGridGraph(
                nbNodesX, nbNodesY, RAND_OFFSET, HAS_DIAGONALS);
        this.graph = graphInit;

        this.graph = GraphFactory.generateGridGraph2(graphInit, PROBABILITY_DISABLE_EDGE);

        // SETUP PATHFINDING
        this.pathAlgorithms.add(new AStar(heuristics));
        this.pathAlgorithms.add(new LRTAStar(heuristics, LOOKAHEAD));
        this.pathAlgorithms.add(new TBAStar(heuristics, MAX_STEP_EXPANSION, MAX_STEP_BACKTRACKING));

        this.pathfindingGenerator = new PathGenerator(graph, pathAlgorithms, MIN_LENGHT_STEP);



        if (GRAPHICS_ENABLED) {

            /// SETUP GRAPH CANVAS

            this.pathfindingGenerator.notifyObserver = this::updateAndRepaintGraph;

            canvas = new GraphCanvas(graph,
                    CANVAS_WIDTH,
                    CANVAS_HEIGHT,
                    (int)GRAPH_OFFSET.x,
                    (int)GRAPH_OFFSET.y,
                    DISPLAY_VISITED);


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
        else {
            this.pathfindingGenerator.notifyObserver = () -> {
                //empty, il n'y a personne à notifier car plus de partie graphique
            };
        }


        //Start generating random pathfinding
        pathfindingGenerator.scheduleNextTimer();

    }

    private void updateAndRepaintGraph() {

        canvas.setPath(pathfindingGenerator.getPathfindingAlgorithm().getPathToDisplay());
        canvas.setVisited(pathfindingGenerator.getPathfindingAlgorithm().getVisited());
        canvas.setStart(pathfindingGenerator.getStart());
        canvas.setGoal(pathfindingGenerator.getGoal());
        canvas.setCurrent(pathfindingGenerator.getCurrent());
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
