package nl.aylin.gameoflife.clock;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameClock {
    private static final int MIN_DELAY_MILLIS = 50;
    private static final int MAX_DELAY_MILLIS = 2000;

    // Observer pattern: listeners abonneren zich en krijgen bij elke tick een melding.
    private final List<TickListener> subscribers = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> task;
    private long tickNumber;
    private int delayMillis = 500;

    public synchronized void start() {
        // Start begint opnieuw bij tick 0.
        tickNumber = 0;
        startScheduler();
    }

    public synchronized void pause() {
        stopScheduler();
    }

    public synchronized void resume() {
        if (!isRunning()) {
            startScheduler();
        }
    }

    public synchronized void stop() {
        stopScheduler();
        tickNumber = 0;
    }

    public synchronized void faster() {
        delayMillis = Math.max(MIN_DELAY_MILLIS, delayMillis / 2);
        restartIfRunning();
    }

    public synchronized void slower() {
        delayMillis = Math.min(MAX_DELAY_MILLIS, delayMillis * 2);
        restartIfRunning();
    }

    public void addSubscriber(TickListener listener) {
        subscribers.add(listener);
    }

    public void removeSubscriber(TickListener listener) {
        subscribers.remove(listener);
    }

    public synchronized long getTickNumber() {
        return tickNumber;
    }

    public synchronized int getDelayMillis() {
        return delayMillis;
    }

    public synchronized boolean isRunning() {
        return task != null && !task.isCancelled() && !task.isDone();
    }

    public void tickOnce() {
        long currentTick;
        synchronized (this) {
            // Eerst ticknummer verhogen, daarna alle subscribers informeren.
            tickNumber++;
            currentTick = tickNumber;
        }
        for (TickListener subscriber : subscribers) {
            subscriber.onTick(currentTick);
        }
    }

    private void startScheduler() {
        stopScheduler();
        // ScheduledExecutorService laat automatisch elke delayMillis een tick uitvoeren.
        executor = Executors.newSingleThreadScheduledExecutor();
        task = executor.scheduleAtFixedRate(this::tickOnce, delayMillis, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void stopScheduler() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private synchronized void restartIfRunning() {
        if (isRunning()) {
            startScheduler();
        }
    }
}
