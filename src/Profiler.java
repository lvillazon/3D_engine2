import java.util.ArrayList;

public class Profiler {
    // used to time the processes in the render pipeline
    private long startTime;     // when timing begins
    private long duration;      // duration in ns for a single timing
    private long totalDuration; // accumulated time for several timings
    private int averageOver;    // how many samples to use for averaging
    private int sampleCounter;  // how many samples have been timed for this averaging block
    private String name;        // descriptive label of the process being profiled
    private ArrayList<Profiler> subProfilers;  // times for subprocesses, to measure their % contribution

    public Profiler(String name, int averageOver) {
        this.name = name;
        startTime = 0;
        duration = -1;
        totalDuration = 0;
        this.averageOver = averageOver;
        sampleCounter = 0;
        subProfilers = new ArrayList<>();
    }

    public void start() {
        startTime = System.nanoTime();
        sampleCounter++;
    }

    public void stop() {
        duration = System.nanoTime() - startTime;
        if (sampleCounter > averageOver) {
            sampleCounter = 0;
            totalDuration = 0;
        }
        totalDuration += duration;
    }

    public Profiler addSubProfiler(String name) {
        Profiler sub = new Profiler(name, averageOver);
        subProfilers.add(sub);
        return sub;
    }

    public void display(String terminator) {
        if (sampleCounter == averageOver) {  // only bother printing when a new sampling block is complete
            double averageDuration = (double) totalDuration / averageOver / 1000000;
            if (subProfilers.size() > 0) {
                // break down times for each sub process too
                System.out.printf(name + ": %5.2fms (", averageDuration);
                double remainingPercentage = 100.0;
                for (Profiler p: subProfilers) {
                    double percentage = p.getPercent(averageDuration);
                    remainingPercentage -= percentage;
                    System.out.printf(p.name + ": %4.1f%%, ", percentage);
                }
                System.out.printf("other: %4.1f%%)"+terminator, remainingPercentage);
            } else {
                // simple display for a single profiler
                System.out.printf(name + ": %5.2fms"+terminator, averageDuration);
            }
        }
    }

    public void print() {
        display("  ");
    }

    public void println() {
        display("%n");
    }

    public double getPercent(double parentDuration) {
        // returns the time taken by this process as a % of the parentDuration
        double averageDuration = (double) totalDuration / averageOver / 1000000;
        return averageDuration / parentDuration * 100;
//        return (double) totalDuration / (parentDuration * 100000);
    }
 }
