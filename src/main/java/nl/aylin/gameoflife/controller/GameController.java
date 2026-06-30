package nl.aylin.gameoflife.controller;

import nl.aylin.gameoflife.clock.GameClock;
import nl.aylin.gameoflife.clock.TickListener;
import nl.aylin.gameoflife.model.CellType;
import nl.aylin.gameoflife.model.GameBoard;
import nl.aylin.gameoflife.model.Position;

public class GameController implements TickListener {
    private final GameBoard board;
    private final GameClock clock;
    private Runnable changeListener = () -> { };

    public GameController() {
        this(new GameBoard(), new GameClock());
    }

    public GameController(GameBoard board, GameClock clock) {
        this.board = board;
        this.clock = clock;
        this.clock.addSubscriber(this);
    }

    public GameBoard getBoard() {
        return board;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    public void start() {
        clock.start();
        notifyChanged();
    }

    public void pause() {
        clock.pause();
        notifyChanged();
    }

    public void resume() {
        clock.resume();
        notifyChanged();
    }

    public void stop() {
        clock.stop();
        notifyChanged();
    }

    public void reset() {
        clock.stop();
        board.clear();
        notifyChanged();
    }

    public void faster() {
        clock.faster();
        notifyChanged();
    }

    public void slower() {
        clock.slower();
        notifyChanged();
    }

    public void placeCell(CellType type, Position position) {
        clock.pause();
        board.addCell(type, position);
        notifyChanged();
    }

    public void removeCell(Position position) {
        clock.pause();
        board.removeCell(position);
        notifyChanged();
    }

    public long getTickNumber() {
        return clock.getTickNumber();
    }

    public long countCells(CellType type) {
        return board.countCells(type);
    }

    @Override
    public void onTick(long tickNumber) {
        board.nextGeneration();
        notifyChanged();
    }

    private void notifyChanged() {
        changeListener.run();
    }
}
