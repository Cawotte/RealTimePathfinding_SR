package uqac.graph.view;

import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.IRealTimePathfinding;
import uqac.graph.pathfinding.PathNotFoundException;
import uqac.graph.pathfinding.logs.LogPathfinding;

import java.util.*;

public class PathGenerator {

    private WeightedGraph graph;

    private ArrayList<IRealTimePathfinding> pathAlgorithms;
    private IRealTimePathfinding currentlyUsedAlgorithm;

    private ArrayList<IRealTimePathfinding> cumulatedLogTargets = new ArrayList<>();
    private String comparisonText = "";

    private long minTimePathfinding;
    private boolean manualContinue;

    private Scanner sc = new Scanner(System.in);

    //Temps entre calcul d'un chemin entre une nouvelle paire de noeuds
    private int delay = 2;

    public Runnable notifyObserver;

    private Node start;
    private Node goal;

    public PathGenerator(WeightedGraph graph, ArrayList<IRealTimePathfinding> pathfindingAlgorithms, long minTimeStep, boolean manualContinue) {
        this.graph = graph;
        this.pathAlgorithms = pathfindingAlgorithms;
        this.minTimePathfinding = minTimeStep;
        this.manualContinue = manualContinue;
    }

    /**
     * Démarre une nouvelle comparaison après delay.
     */
    public void scheduleNextTimer() {

        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TaskRandomPath();
        //repeat
        timer.schedule(recurringPathfinding,delay * 1000);
    }

    /**
     * Commence une comparaison de chaque algorithmes entre le start/goal donné.
     * @param start
     * @param goal
     * @param minStepTime
     * @throws PathNotFoundException
     */
    private void startRealTimePathfinding(Node start, Node goal, long minStepTime) throws PathNotFoundException {

        this.start = start;
        this.goal = goal;

        cumulatedLogTargets = new ArrayList<>();
        comparisonText = "";

        System.out.println("NEXT PATH COMPARISONS : \n");

        //For each registered algorithms
        for (IRealTimePathfinding algorithm : pathAlgorithms) {

            //Clean garbage
            Runtime.getRuntime().gc();

            currentlyUsedAlgorithm = algorithm;
            cumulatedLogTargets.add(currentlyUsedAlgorithm);

            //Init le pathfinding
            algorithm.beginPathfinding(start, goal);

            //Tant que c'est pas fini, next step.
            while (!algorithm.hasFinished()) {

                //Used to force min step length
                long startTime = System.currentTimeMillis();

                //execution de l'agorithme
                algorithm.getNextStep(); // this.current =
                notifyObserver.run(); //Notification vers la partie graphique

                //Force min step length to monitor execution
                long timeStepElapsed = System.currentTimeMillis() - startTime;

                long timeRemaining = minStepTime - timeStepElapsed;
                if (timeRemaining > 0) {
                    try {
                        Thread.sleep(timeRemaining);
                    }
                    catch (InterruptedException ex) {
                        System.out.println(ex.toString());
                        ex.printStackTrace();
                    }
                }
            }

            comparisonText = getCumulatedComparisonText();
            notifyObserver.run();

            System.out.println(algorithm.getLog().toString());
        }

        //print comparisons
        System.out.println(getCumulatedComparisonText());

    }


    public IRealTimePathfinding getPathfindingAlgorithm() {
        return currentlyUsedAlgorithm;
    }

    public String getCumulatedLogText() {
        String str = "";
        for (IRealTimePathfinding algorithm : cumulatedLogTargets) {
            str = str.concat(algorithm.getLog().toString()) + "\n\n";
        }
        return str;
    }

    public String getComparisonText() {
        return comparisonText;
    }
    private String getCumulatedComparisonText() {
        ArrayList<LogPathfinding> logs = new ArrayList<>();
        for (IRealTimePathfinding algorithm : cumulatedLogTargets) {
            logs.add(algorithm.getLog());
        }
        return LogPathfinding.compareLogToString(logs);
    }

    private void waitKeypressToContinue() {
        System.out.println("Wait keypress to continue...");
        sc.next();
    }



    class TaskRandomPath extends TimerTask {

        public void run() {
            try {

                start = graph.getRandomNode();
                goal = graph.getRandomNode();

                startRealTimePathfinding(start, goal, minTimePathfinding);


            }
            catch (PathNotFoundException pnfe) {
                System.out.println("There's no path between the two nodes !");
            }
            catch (Exception ex) {
                System.out.println("Error running thread " + ex.getMessage());
                ex.printStackTrace();
            }

            if (manualContinue) {
                waitKeypressToContinue();
            }

            //relaunch
            scheduleNextTimer();
        }
    }


}
