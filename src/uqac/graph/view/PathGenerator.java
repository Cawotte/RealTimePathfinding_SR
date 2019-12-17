package uqac.graph.view;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.IRealTimePathfinding;
import uqac.graph.pathfinding.Path;
import uqac.graph.pathfinding.PathNotFoundException;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

public class PathGenerator {

    private WeightedGraph graph;

    private ArrayList<IRealTimePathfinding> pathAlgorithms;
    private IRealTimePathfinding currentlyUsedAlgorithm;

    private ArrayList<IRealTimePathfinding> cumulatedLogTargets = new ArrayList<>();

    private long minTimePathfinding;
    private boolean manualContinue;

    private Scanner sc = new Scanner(System.in);

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

    public void scheduleNextTimer() {

        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TaskRandomPath();
        //repeat
        timer.schedule(recurringPathfinding,delay * 1000);
    }

    private void startRealTimePathfinding(Node start, Node goal, long minStepTime) throws PathNotFoundException {

        this.start = start;
        this.goal = goal;
        //this.current = start;

        cumulatedLogTargets = new ArrayList<>();

        System.out.println("START NEW PATH CALCULATION\n");
        //For each registered algorithms
        for (IRealTimePathfinding algorithm : pathAlgorithms) {

            Runtime.getRuntime().gc();


            currentlyUsedAlgorithm = algorithm;
            cumulatedLogTargets.add(currentlyUsedAlgorithm);

            System.out.println(algorithm.toString());

            algorithm.beginPathfinding(start, goal);

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

            notifyObserver.run();

            System.out.println(algorithm.getLog().toString());
        }

        comparePathsFound();

        System.out.println("\nEND PATH CALCULATIONS\n");

    }


    public IRealTimePathfinding getPathfindingAlgorithm() {
        return currentlyUsedAlgorithm;
    }

    public String getCumulatedLogText() {
        String str = "";
        for (IRealTimePathfinding algorithm : cumulatedLogTargets) {
            str = str.concat(algorithm.toString() + "\n\n" + algorithm.getLog().toString()) + "\n\n";
        }
        return str;
    }

    private void waitKeypressToContinue() {
        System.out.println("Wait keypress to continue...");
        sc.next();
    }


    private void comparePathsFound() {

        Path optimalPath = pathAlgorithms.get(0).getPathWalked();

        DecimalFormat df = new DecimalFormat("##.##%");

        System.out.println("Comparing different algorithms Suboptimality : ");
        for (IRealTimePathfinding algorithm : pathAlgorithms) {
            float suboptimality = algorithm.getPathWalked().compareSuboptimality(optimalPath);
            System.out.println("Suboptimality : " + df.format(suboptimality) + " - " + algorithm.toString() );
        }
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
