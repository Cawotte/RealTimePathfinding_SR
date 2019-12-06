package uqac.graph.view;

import uqac.graph.INode;
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

    private long minTimePathfinding = 2000L;

    private int frequencyRandomPathCalculator = 2;

    public Runnable notifyObserver;

    private Node start;
    private Node goal;
    private INode current;

    public PathCalculator(WeightedGraph graph, IRealTimePathfinding pathfindingAlgorithm) {
        this.graph = graph;
        this.pathfindingAlgorithm = pathfindingAlgorithm;
    }

    public void scheduleNextTimer() {

        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TaskRandomPath();
        //repeat
        timer.schedule(recurringPathfinding,frequencyRandomPathCalculator * 1000);
    }

    private void startRealTimePathfinding(Node start, Node goal, long minStepTime) throws PathNotFoundException {

        this.start = start;
        this.goal = goal;
        this.current = start;

        pathfindingAlgorithm.beginPathfinding(start, goal);

        while (!pathfindingAlgorithm.hasFinished()) {

            long startTime = System.currentTimeMillis();

            this.current = pathfindingAlgorithm.getNextStep();

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

    public INode getStart() {
        return start;
    }

    public INode getGoal() {
        return goal;
    }

    public INode getCurrent() {
        return current;
    }


    class TaskRandomPath extends TimerTask {

        public void run() {
            try {

                start = graph.getRandomNode();
                goal = graph.getRandomNode();

                startRealTimePathfinding(start, goal, 50);


            }
            catch (PathNotFoundException pnfe) {
                System.out.println("AStar : Path not found!");
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
