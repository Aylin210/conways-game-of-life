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

    // Iedereen in deze lijst krijgt een seintje wanneer de klok tikt.
    private final List<TickListener> listeners = new CopyOnWriteArrayList<>();

    // Deze twee objecten zorgen ervoor dat tickOnce automatisch herhaald wordt.
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> task;

    // tickNumber is hoeveel stappen de simulatie al heeft gedaan.
    private long tickNumber;

    // Hoe lager dit getal, hoe sneller de simulatie loopt.
    private int delayMillis = 500;

    public synchronized void start() {
        // Start begint altijd opnieuw bij tick 0.
        tickNumber = 0;
        startScheduler();
    }

    public synchronized void pause() {
        // Pauze stopt de automatische ticks, maar bewaart het ticknummer.
        stopScheduler();
    }

    public synchronized void resume() {
        // Hervat alleen als de klok nu niet loopt.
        if (!isRunning()) {
            startScheduler();
        }
    }

    public synchronized void stop() {
        // Stop is sterker dan pauze: ook de teller gaat terug naar 0.
        stopScheduler();
        tickNumber = 0;
    }

    public synchronized void faster() {
        // Twee keer zo snel, maar nooit sneller dan MIN_DELAY_MILLIS.
        delayMillis = Math.max(MIN_DELAY_MILLIS, delayMillis / 2);
        restartIfRunning();
    }

    public synchronized void slower() {
        // Twee keer zo langzaam, maar nooit langzamer dan MAX_DELAY_MILLIS.
        delayMillis = Math.min(MAX_DELAY_MILLIS, delayMillis * 2);
        restartIfRunning();
    }

    public void addSubscriber(TickListener listener) {
        listeners.add(listener);
    }

    public void removeSubscriber(TickListener listener) {
        listeners.remove(listener);
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
            // Eerst de teller verhogen.
            tickNumber++;
            currentTick = tickNumber;
        }

        // Daarna iedereen vertellen dat er een tick was.
        for (TickListener listener : listeners) {
            listener.onTick(currentTick);
        }
    }

    private void startScheduler() {
        // Eerst oude timer stoppen, zodat er nooit twee timers tegelijk lopen.
        stopScheduler();

        // Vanaf nu wordt tickOnce elke delayMillis milliseconden aangeroepen.
        executor = Executors.newSingleThreadScheduledExecutor();
        task = executor.scheduleAtFixedRate(this::tickOnce, delayMillis, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void stopScheduler() {
        // Stop de herhalende taak.
        if (task != null) {
            task.cancel(false);
            task = null;
        }

        // Stop ook de thread die de taak uitvoerde.
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private void restartIfRunning() {
        // Als de snelheid verandert terwijl de klok loopt,
        // moeten we opnieuw starten met de nieuwe delay.
        if (isRunning()) {
            startScheduler();
        }
    }
}
