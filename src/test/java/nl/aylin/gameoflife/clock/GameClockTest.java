package nl.aylin.gameoflife.clock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameClockTest {
    @Test
    void subscriberReceivesTicks() {
        // Controleert het Observer pattern: een subscriber krijgt een tick.
        GameClock clock = new GameClock();
        List<Long> receivedTicks = new ArrayList<>();
        clock.addSubscriber(receivedTicks::add);

        clock.tickOnce();

        assertEquals(List.of(1L), receivedTicks);
    }

    @Test
    void multipleSubscribersReceiveTicks() {
        // Controleert dat meerdere subscribers dezelfde tick ontvangen.
        GameClock clock = new GameClock();
        List<Long> firstSubscriber = new ArrayList<>();
        List<Long> secondSubscriber = new ArrayList<>();
        clock.addSubscriber(firstSubscriber::add);
        clock.addSubscriber(secondSubscriber::add);

        clock.tickOnce();

        assertEquals(List.of(1L), firstSubscriber);
        assertEquals(List.of(1L), secondSubscriber);
    }

    @Test
    void tickNumberIncreases() {
        // Controleert dat het ticknummer bij elke tick wordt verhoogd.
        GameClock clock = new GameClock();

        clock.tickOnce();
        clock.tickOnce();

        assertEquals(2, clock.getTickNumber());
    }
}
