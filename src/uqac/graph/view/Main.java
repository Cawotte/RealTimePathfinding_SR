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

    //Nombre de noeuds du graph
    static final int NB_NODES = 100000;

    //Offset (x,y) aléatoire sur lesquels chaque point de la grille seronts déplacés
    static final Vector2 RAND_OFFSET = new Vector2(0.2f, 0.2f);

    //Connection diagonale entre les noeuds
    static final boolean HAS_DIAGONALS = true;

    //Probabilité que chaque noeud disparaissent après génération du Graph,
    //0 = Aucun obstacle, tout est parfaitement connecté
    //0.5 = La moitié des connections disparaissent, donnant beaucoup d'obstacles.
    static final float PROBABILITY_DISABLE_EDGE = 0.3f;

    //CANVAS GRAPH CONSTANT

    //Activer/Désactiver l'affichage graphique, activer pour suivre l'exécution, mais à désactiver pour de vrai évaluation de performances
    //sur de grand graph
    static final boolean GRAPHICS_ENABLED = true;

    //Afficher les noeuds visités? (Jaune). Peut entrainer des erreurs d'accès en
    //true sur des très gros graph.
    static final boolean DISPLAY_VISITED = true;

    //Si vrai attend une entrée utilisateur avant chaque exécution, si faux continue automatiquement le calcul de nouveaux chemins.
    static final boolean MANUAL_CONTINUE = false;

    //Taille de la fenetre graphique
    static final int CANVAS_WIDTH  = 960;
    static final int CANVAS_HEIGHT = 600;
    //Taille de la bordure vide de la fenetre graphique
    static final Vector2 GRAPH_OFFSET = new Vector2(200, 10);

    //Nombre de milliseconde minimal par étape de l'algorithme.
    //Mettre 0 pour la performance, et entre 10 et 100 pour voir le déroulement de l'algo avec l'affichage
    static final int MIN_LENGHT_STEP = 0;

    //HEURISTIC
    //fonction heuristic utilisés, deux disponibles dans Heuristics : euclidianDistance et manhattanDistance
    static final BiFunction<Node, Node, Float> heuristics = Heuristics::euclidianDistance;

    //PATHFINDING TAB* PARAMETERS
    static final int MAX_STEP_EXPANSION = 100;
    static final int MAX_STEP_BACKTRACKING = 500;

    //LRTA* PARAMETERS
    static final int LOOKAHEAD = 5;

    private String parametersString = "";

    private GraphCanvas canvas;

    private WeightedGraph graph;
    private PathGenerator pathfindingGenerator;

    private ArrayList<IRealTimePathfinding> pathAlgorithms = new ArrayList<>();


    public Main() {

        //DATA

        //Calcule le ratio H*L requis pour avoir un graph au noeuds équidistant quelque soit le nombre de node et ratio d'écran.
        float aspectRatio = ((float)CANVAS_WIDTH - GRAPH_OFFSET.x * 2) / ((float)CANVAS_HEIGHT - GRAPH_OFFSET.y * 2);

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
        this.pathAlgorithms.add(new TBAStar(heuristics, MAX_STEP_EXPANSION, MAX_STEP_BACKTRACKING));
        this.pathAlgorithms.add(new LRTAStar(heuristics, LOOKAHEAD));

        this.pathfindingGenerator = new PathGenerator(graph, pathAlgorithms, MIN_LENGHT_STEP, MANUAL_CONTINUE);



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

        parametersString = parametersToString(nbNodesX, nbNodesY);
        canvas.setParametersText(parametersString);

        //Start generating random pathfinding
        pathfindingGenerator.scheduleNextTimer();

    }

    private void updateAndRepaintGraph() {

        canvas.updateWithAlgorithm(pathfindingGenerator.getPathfindingAlgorithm());
        canvas.setLogText(pathfindingGenerator.getCumulatedLogText());
        canvas.setCmpText(pathfindingGenerator.getComparisonText());
        this.repaint();
    }

    private String parametersToString(int nbNodesX, int nbNodesY) {
        String str = "GRAPH PARAMETERS :\n";

        str += "Size : " + nbNodesX + "*" + nbNodesY + "\n";
        str += "     = " + nbNodesX * nbNodesY + " Nodes\n";

        str += "Rand Offset : " + RAND_OFFSET.toString() + "\n";
        str += "Disabled Edge : " + String.format("%.0f%%", PROBABILITY_DISABLE_EDGE * 100f) + "\n";

        if (HAS_DIAGONALS) {
            str += "HAS diagonals\n";
        }
        if (DISPLAY_VISITED) {
            str += "DISPLAY visited nodes\n";
        }
        if (MANUAL_CONTINUE) {
            str += "Next Path : ON KEYPRESS\n";
        }
        else {
            str += "Next Path : AUTO\n";
        }

        str += "Minimal Time-Step : " + MIN_LENGHT_STEP + "ms\n\n";

        str += "ALGORITHMS :\n";
        for (IRealTimePathfinding algo : pathAlgorithms) {
            str += "  " + algo.toString() + "\n";
        }

        return str;
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
    }
}
