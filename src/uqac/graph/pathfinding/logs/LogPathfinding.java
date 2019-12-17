package uqac.graph.pathfinding.logs;

import javafx.util.Pair;
import uqac.graph.pathfinding.Path;

import java.rmi.Remote;
import java.util.ArrayList;

public class LogPathfinding {


    private static final long MEGABYTE = 1024L * 1024L;

    //logs
    private ArrayList<Long> stepTime = new ArrayList<>();
    private ArrayList<Long> stepMemory = new ArrayList<>();

    //algo
    private String algorithmName = "";
    //Time
    private long startingTime;
    private long cumulatedTimeElapsed;
    private long totalTime;
    //Step time
    private long shortestStep;
    private long longestStep;
    private long averageStep;

    //Memory
    private long startingMemory;
    private long maxMemory;

    private Path path;

    public void startLogging(String algorithmName, Path path) {
        startingTime = System.currentTimeMillis();
        startingMemory = getUsedMemory();

        //Reset values

        //Logs
        stepTime = new ArrayList<>();
        stepMemory = new ArrayList<>();
        cumulatedTimeElapsed = 0L;
        maxMemory = 0L;

        //step times
        this.shortestStep = Long.MAX_VALUE;
        this.longestStep = Long.MIN_VALUE;
        this.averageStep = 0L;

        this.algorithmName = algorithmName;
        this.path = path;

    }

    public void addStep() {

        //Time since last step
        long timeElapsed = System.currentTimeMillis() - (startingTime + cumulatedTimeElapsed);
        stepTime.add(timeElapsed);
        cumulatedTimeElapsed += timeElapsed;

        if (timeElapsed > longestStep) {
            longestStep = timeElapsed;
        }
        if (timeElapsed < shortestStep) {
            shortestStep = timeElapsed;
        }

        //Total time
        totalTime = System.currentTimeMillis() - startingTime;
        //Average
        this.averageStep = totalTime / stepTime.size();

        //Memory usage since beginning
        long memoryUsage = getUsedMemory() - startingMemory;
        stepMemory.add(memoryUsage);

        //remember max memory
        maxMemory = Math.max(maxMemory, memoryUsage);

    }

    public void finishLogging(Path path) {
        addStep();
        totalTime = System.currentTimeMillis() - startingTime;
        this.path = path;

        //Get mean steps
        this.averageStep = totalTime / stepTime.size();

        //Garbage collection at end of logging
        Runtime.getRuntime().gc();
    }

    static public String logsComparison(ArrayList<LogPathfinding> logs) {

        LogPathfinding firstLog = logs.get(0);
        Pair<String, Long> bestAverage = new Pair<String, Long>(firstLog.algorithmName, firstLog.averageStep);
        Pair<String, Long> bestTime = new Pair<String, Long>(firstLog.algorithmName, firstLog.totalTime);
        Pair<String, Long> bestMemory = new Pair<String, Long>(firstLog.algorithmName, firstLog.maxMemory);
        Pair<String, Float> bestPath = new Pair<String, Float>(firstLog.algorithmName, firstLog.path.getCost());

        for (LogPathfinding log : logs) {

            //Fastest average
            if (log.averageStep < bestAverage.getValue()) {
                bestAverage = new Pair<>(log.algorithmName, log.averageStep);
            }

            //Fastest Computing
            if (log.totalTime < bestTime.getValue()) {
                bestTime = new Pair<>(log.algorithmName, log.totalTime);
            }

            //Smallest Memory Usage
            if (log.maxMemory < bestMemory.getValue()) {
                bestMemory = new Pair<>(log.algorithmName, log.maxMemory);
            }

            //Best Path
            if (log.path.getCost() < bestPath.getValue()) {
                bestPath = new Pair<>(log.algorithmName, log.path.getCost());
            }
        }

        //We found the best of each algorithms, now we build a String with the results

        return "";
    }

    @Override
    public String toString() {
        String str = "";
        str += "  " + algorithmName + "\n\n";
        str += "Iterations :    "+stepTime.size() + "\n";
        if (path != null) {
            //str += "\n";
            str += "Path Lenght :   " + path.getSize() + "\n";
            str += "Path Cost :     " + String.format("%.02f",path.getCost()) + "\n";
        }
        //str += "\n";
        str += "Total Time :    " + totalTime + "ms\n";
        str += "Average Step :  " + averageStep + "ms\n" +
                "Longest Step :  " + longestStep + "ms\n" +
                "Shortest Step : " + shortestStep + "ms\n";
        //str += "\n";
        str += "Memory Usage :  " + bytesToMegabytes(maxMemory) + " Mb\n";

        return str;
    }


    private long getUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }



    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

}
