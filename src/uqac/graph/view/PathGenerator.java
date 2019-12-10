package uqac.graph.view;

import uqac.graph.INode;
import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.IRealTimePathfinding;
import uqac.graph.pathfinding.Path;
import uqac.graph.pathfinding.PathNotFoundException;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PathGenerator {

    private WeightedGraph graph;

    private ArrayList<IRealTimePathfinding> pathAlgorithms;
    private IRealTimePathfinding currentlyUsedAlgorithm;

    private long minTimePathfinding;

    private int delay = 2;

    public Runnable notifyObserver;

    private Node start;
    private Node goal;
    private INode current;

    public PathGenerator(WeightedGraph graph, IRealTimePathfinding pathfindingAlgorithm, long minTimeStep) {
        this.graph = graph;
        this.pathAlgorithms = new ArrayList<>();
        this.pathAlgorithms.add(pathfindingAlgorithm);
        this.minTimePathfinding = minTimeStep;
    }

    public PathGenerator(WeightedGraph graph, ArrayList<IRealTimePathfinding> pathfindingAlgorithms, long minTimeStep) {
        this.graph = graph;
        this.pathAlgorithms = pathfindingAlgorithms;
        this.minTimePathfinding = minTimeStep;
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
        this.current = start;

        System.out.println("START NEW PATH CALCULATION\n");
        //For each registered algorithms
        for (IRealTimePathfinding algorithm : pathAlgorithms) {

            Runtime.getRuntime().gc();

            currentlyUsedAlgorithm = algorithm;

            System.out.println(algorithm.toString());

            algorithm.beginPathfinding(start, goal);

            while (!algorithm.hasFinished()) {

                //Used to force min step length
                long startTime = System.currentTimeMillis();

                //execution de l'agorithme
                this.current = algorithm.getNextStep();
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
        //TODO : Comparer logs
    }


    public IRealTimePathfinding getPathfindingAlgorithm() {
        return currentlyUsedAlgorithm;
    }

    public INode getStart() {
        return start;
    }

    public INode getGoal() {
        return goal;
    }

    public INode getCurrent() {
        return current;
    }

    private void comparePathsFound() {

        Path optimalPath = pathAlgorithms.get(0).getPathWalked();

        DecimalFormat df = new DecimalFormat("##.##%");

        System.out.println("Comparing different algorithms Suboptimality : ");
        for (IRealTimePathfinding algorithm : pathAlgorithms) {
            float suboptimality = algorithm.getPathWalked().compareSuboptimality(optimalPath);
            System.out.println("Suboptimality : " + df.format(suboptimality) + "% - " + algorithm.toString() );
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

            //relaunch
            scheduleNextTimer();
        }
    }


}
