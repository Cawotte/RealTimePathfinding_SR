package uqac.graph.pathfinding.logs;

import uqac.graph.pathfinding.Path;

import java.util.ArrayList;

public class LogPathfinding {


    private static final long MEGABYTE = 1024L * 1024L;

    //logs
    private ArrayList<Long> stepTime = new ArrayList<>();
    private ArrayList<Long> stepMemory = new ArrayList<>();

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

    public void startLogging() {
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
        this.averageStep = 0;
        for (int i = 0; i < stepTime.size(); i++) {
            this.averageStep += stepTime.get(i);
        }
        this.averageStep = this.averageStep / stepTime.size();

        //Garbage collection at end of logging
        Runtime.getRuntime().gc();
    }

    @Override
    public String toString() {
        String str = "";
        str += "Iterations : " + stepTime.size() + "\n";
        str += "Path Lenght : " + path.getSize() + ", Cost : " + path.getCost() + "\n";
        str += "Total Time : " + totalTime + "ms\n";
        str += "Average Step : " + averageStep + "ms, Longest Step : " + longestStep + "ms, Shortest Step : " + shortestStep + "ms\n";
        str += "Memory Usage : " + maxMemory + " bytes / " + bytesToMegabytes(maxMemory) + " Mb\n";

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
