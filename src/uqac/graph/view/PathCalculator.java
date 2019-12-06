package uqac.graph.view;

import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.IRealTimePathfinding;
import uqac.graph.pathfinding.Path;
import uqac.graph.pathfinding.PathNotFoundException;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PathCalculator {

    private WeightedGraph graph;
    private IRealTimePathfinding pathfindingAlgorithm;
    private Path pathFound;

    private long minTimePathfinding = 2000L;

    private int frequencyRandomPathCalculator = 2;

    public Runnable notifyObserver;

    public PathCalculator(WeightedGraph graph, IRealTimePathfinding pathfindingAlgorithm) {
        this.graph = graph;
        this.pathfindingAlgorithm = pathfindingAlgorithm;
    }

    public void startRecurrentPathfinding() {


        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TaskRandomPath();
            //repeat
        timer.schedule(recurringPathfinding,frequencyRandomPathCalculator * 1000);

    }

    public void scheduleNextTimer() {

        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TaskRandomPath();
        //repeat
        timer.schedule(recurringPathfinding,frequencyRandomPathCalculator * 1000);
    }

    private void startRealTimePathfinding(Node start, Node goal, long minStepTime) {

        pathfindingAlgorithm.beginPathfinding(start, goal);

        while (!pathfindingAlgorithm.hasFinished()) {

            long startTime = System.currentTimeMillis();

            Node nextMove = pathfindingAlgorithm.getNextStep();

            notifyObserver.run();

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

        System.out.println(pathfindingAlgorithm.getLog().toString());

    }


    public IRealTimePathfinding getPathfindingAlgorithm() {
        return pathfindingAlgorithm;
    }


    class TaskRandomPath extends TimerTask {

        public void run() {
            try {

                Node randomNode1 = graph.getRandomNode();
                Node randomNode2 = graph.getRandomNode();

                startRealTimePathfinding(randomNode1, randomNode2, 0);
                /*
                try {
                    pathFound = pathfindingAlgorithm.computeFullPath(randomNode1, randomNode2);
                    System.out.println(pathfindingAlgorithm.getLog().toString());
                }
                catch (PathNotFoundException exc) {
                    System.out.println("Chemin non trouvé! Les noeuds sont inacessibles.\n");
                }

                notifyObserver.run(); */

                //relaunch
                scheduleNextTimer();

            } catch (Exception ex) {
                System.out.println("Error running thread " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }


}
