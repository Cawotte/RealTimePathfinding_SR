package uqac.graph.pathfinding.logs;

import uqac.graph.pathfinding.Path;

import java.util.ArrayList;

public class LogPathfinding {


    private static final long MEGABYTE = 1024L * 1024L;

    private long startingTime;
    private long cumulatedTimeElapsed;
    private long totalTime;

    private ArrayList<Long> stepTime = new ArrayList<>();
    private ArrayList<Long> stepMemory = new ArrayList<>();

    private long startingMemory;
    private long maxMemory;

    private Path path;

    public void startLogging() {
        startingTime = System.currentTimeMillis();
        startingMemory = getUsedMemory();

        //Reset values
        stepTime = new ArrayList<>();
        stepMemory = new ArrayList<>();
        cumulatedTimeElapsed = 0L;
        maxMemory = 0L;

        //Add Step 0
        stepTime.add(0L);
        stepMemory.add(0L);
    }

    public void addStep() {

        //Time since last step
        long timeElapsed = System.currentTimeMillis() - (startingTime + cumulatedTimeElapsed);
        stepTime.add(timeElapsed);
        cumulatedTimeElapsed += timeElapsed;


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
    }

    @Override
    public String toString() {
        String str = "";
        str += "Nb Steps : " + stepTime.size() + "\n";
        str += "Path Lenght : " + path.getSize() + "\n";
        str += "Total Execution Time : " + totalTime + "ms\n";
        str += "Max Memory Usage : " + maxMemory + " bytes / " + bytesToMegabytes(maxMemory) + " Mb\n";

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
