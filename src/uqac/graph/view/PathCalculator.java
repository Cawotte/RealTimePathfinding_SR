package uqac.graph.view;

import uqac.graph.Node;
import uqac.graph.WeightedGraph;
import uqac.graph.pathfinding.IRealTimePathfinding;
import uqac.graph.pathfinding.Path;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PathCalculator {

    private WeightedGraph graph;
    private IRealTimePathfinding pathfindingAlgorithm;
    private Path pathFound;

    private float frequencyRandomPathCalculator = 3f;


    public Runnable notifyObserver;

    public PathCalculator(WeightedGraph graph, IRealTimePathfinding pathfindingAlgorithm) {
        this.graph = graph;
        this.pathfindingAlgorithm = pathfindingAlgorithm;
    }

    public void startRecurrentPathfinding() {


        Timer timer = new Timer();

        //setup timer path
        TimerTask recurringPathfinding = new TimerComputePathfinding();
            //repeat
        timer.schedule(recurringPathfinding,
                (int)frequencyRandomPathCalculator * 1000,
                (int)frequencyRandomPathCalculator * 1000);

    }

    public Path getPathFound() {
        return pathFound;
    }


    class TimerComputePathfinding extends TimerTask {

        public void run() {
            try {

                //Get a random node to disable
                Node randomNode1 = graph.getRandomNode();
                Node randomNode2 = graph.getRandomNode();

                if (randomNode1 == null || randomNode2 == null ) return;

                pathFound = pathfindingAlgorithm.computeFullPath(randomNode1, randomNode2);

                notifyObserver.run();

            } catch (Exception ex) {
                System.out.println("Error running thread " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }


}
