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

    public void startLogging(Path path) {
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

    @Override
    public String toString() {
        String str = "";
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

    private static String fixedLengthString(String string, int length) {


        char fill = ' ';

        return new String(new char[length - string.length()]).replace('\0', fill) + string;

        //return String.format("%1$"+length+ "s", string);
    }

    private long getUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }



    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

}
