package bytecrawl.evtj.utils;

public class EvtJBalancer {

    private final int WINDOW_SIZE = 100;
    private long inactivity = 0;
    private long SLEEP_TIME = 500;
    private long MAX_INACTIVITY;
    private float inactivityPercentage;

    /**
     * Initialisation of the balancer, after initial warm-up,
     * Automatic benchmark of iterations/nanosecond.
     */
    public EvtJBalancer() {
        long start, end, tmp, diff;
        int i = 0;
        boolean exit = false;

        for (int j = 0; j < 9000000; j++) {
            // Warm-up
        }

        start = System.nanoTime();
        while (!exit) {
            i++;
            tmp = System.nanoTime();
            if ((tmp - start) >= 10000000) {
                exit = true;
            }
        }

        end = System.nanoTime();

        diff = end - start;

        float speed = diff / i;

        // Window size in ms, converted to ns.
        MAX_INACTIVITY = (long) ((WINDOW_SIZE * 1000000) / (speed));

    }

    /**
     * Proportional sleep, based on current inactivity.
     * ONLY if more than 50% of the window time is inactive.
     */
    public void balance() {
        inactivityPercentage = inactivity / MAX_INACTIVITY;
        if (inactivityPercentage > 0.5) {
            try {
                Thread.sleep((long) (SLEEP_TIME * inactivityPercentage));
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Notify the balancer that the cycle has been active
     */
    public void setActive() {
        inactivity = 0;
    }

    /**
     * Called on each new iteration of the busy-waiting.
     * By default the cycle is considered inactive. Activity is always notified,
     * and not assumed.
     */
    public void setCycle() {
        inactivity = (inactivity > MAX_INACTIVITY) ? MAX_INACTIVITY : inactivity + 1;
    }

}
