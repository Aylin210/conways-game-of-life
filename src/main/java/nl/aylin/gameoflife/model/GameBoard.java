package nl.aylin.gameoflife.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import nl.aylin.gameoflife.rules.AlternativeRule;
import nl.aylin.gameoflife.rules.CellRule;
import nl.aylin.gameoflife.rules.ConwayRule;

public class GameBoard {
    public static final int DEFAULT_ROWS = 1000;
    public static final int DEFAULT_COLUMNS = 1000;

    private final int rows;
    private final int columns;
    private final Map<CellType, CellRule> rules;

    // Dit is het hele speelveld.
    // We slaan alleen levende cellen op. Dode cellen staan dus niet in deze map.
    // Key = positie op het bord, value = de cel die daar leeft.
    private final Map<Position, Cell> cells = new HashMap<>();

    public GameBoard() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public GameBoard(int rows, int columns) {
        this(rows, columns, defaultRules());
    }

    public GameBoard(int rows, int columns, Map<CellType, CellRule> rules) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Het bord moet minimaal 1 rij en 1 kolom hebben.");
        }
        if (!rules.keySet().containsAll(List.of(CellType.CONWAY, CellType.ALTERNATIVE))) {
            throw new IllegalArgumentException("Voor elk celtype moet een regel bestaan.");
        }
        this.rows = rows;
        this.columns = columns;
        this.rules = new EnumMap<>(rules);
    }

    public synchronized int getRows() {
        return rows;
    }

    public synchronized int getColumns() {
        return columns;
    }

    public synchronized void addCell(CellType type, Position position) {
        validateInsideBoard(position);

        // Als hier al een cel stond, wordt die gewoon vervangen.
        cells.put(position, new Cell(position, type));
    }

    public synchronized void removeCell(Position position) {
        cells.remove(position);
    }

    public synchronized Optional<Cell> getCell(Position position) {
        // Optional betekent: misschien staat hier een cel, misschien niet.
        return Optional.ofNullable(cells.get(position));
    }


    public synchronized long countCells(CellType type) {
        return cells.values().stream()
                .filter(cell -> cell.getType() == type)
                .count();
    }

    public synchronized int getLivingCellCount() {
        return cells.size();
    }

    public synchronized List<Cell> getCellsSnapshot() {
        // De GUI tekent met een kopie van de lijst.
        // Zo kan de echte map niet per ongeluk veranderd worden tijdens het tekenen.
        return Collections.unmodifiableList(new ArrayList<>(cells.values()));
    }

    public synchronized void clear() {
        cells.clear();
    }

    public synchronized int countLivingNeighbors(Position position) {
        return countNeighbors(position, null);
    }

    public synchronized int countNeighborsOfType(Position position, CellType type) {
        return countNeighbors(position, type);
    }

    public synchronized void nextGeneration() {
        // We bouwen eerst een nieuwe map.
        // Pas aan het einde vervangen we het oude bord.
        // Anders zouden cellen elkaar tijdens het berekenen al beinvloeden.
        Map<Position, Cell> nextRound = new HashMap<>();

        for (Position position : findPlacesToCheck()) {
            Cell cell = cells.get(position);
            int neighbors = countNeighbors(position, null);

            // Er leeft nu al een cel op deze plek.
            if (cell != null) {
                if (cellStaysAlive(cell.getType(), neighbors)) {
                    nextRound.put(position, cell);
                }
            } else {
                // Deze plek is leeg. Misschien wordt hier een nieuwe cel geboren.
                CellType newType = getNewCellType(position);
                if (newType != null) {
                    nextRound.put(position, new Cell(position, newType));
                }
            }
        }

        cells.clear();
        cells.putAll(nextRound);
    }

    private boolean cellStaysAlive(CellType type, int livingNeighbors) {
        // Strategy pattern: GameBoard kent de details van de regel niet.
        // De juiste CellRule beslist zelf of dit celtype overleeft.
        return rules.get(type).survives(livingNeighbors);
    }

    private CellType getNewCellType(Position position) {
        // Een alternatieve cel wordt geboren met precies 4 alternatieve buren.
        // Deze check staat expres eerst: alternatief krijgt voorrang bij een dubbele match.
        if (rules.get(CellType.ALTERNATIVE).isBorn(countNeighbors(position, CellType.ALTERNATIVE))) {
            return CellType.ALTERNATIVE;
        }

        // Een Conway-cel wordt geboren met precies 3 Conway-buren.
        if (rules.get(CellType.CONWAY).isBorn(countNeighbors(position, CellType.CONWAY))) {
            return CellType.CONWAY;
        }

        // Geen regel klopt, dus deze plek blijft leeg.
        return null;
    }

    private int countNeighbors(Position position, CellType type) {
        int count = 0;

        // We lopen door de 8 plekken om de cel heen.
        // rowOffset en columnOffset zijn -1, 0 of 1.
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                // Offset 0,0 is de cel zelf. Die mag niet meetellen als buur.
                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                Position neighbor = position.neighborAt(rowOffset, columnOffset);
                Cell cell = cells.get(neighbor);

                // type == null betekent: tel alle levende buren.
                // Anders tellen we alleen buren van dat ene type.
                if (cell != null && (type == null || cell.getType() == type)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Set<Position> findPlacesToCheck() {
        // Alleen levende cellen en hun buren kunnen veranderen.
        // Een plek ver weg van alle cellen blijft sowieso dood.
        Set<Position> positions = new HashSet<>();
        for (Position position : cells.keySet()) {
            positions.add(position);
            addNeighborPlaces(position, positions);
        }
        return positions;
    }

    private void addNeighborPlaces(Position position, Set<Position> positions) {
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                Position neighbor = position.neighborAt(rowOffset, columnOffset);
                if (isInsideBoard(neighbor)) {
                    positions.add(neighbor);
                }
            }
        }
    }

    private void validateInsideBoard(Position position) {
        // Een cel mag niet buiten het bord geplaatst worden.
        if (!isInsideBoard(position)) {
            throw new IllegalArgumentException("Positie ligt buiten het bord: " + position);
        }
    }

    private boolean isInsideBoard(Position position) {
        return position.getRow() >= 0
                && position.getRow() < rows
                && position.getColumn() >= 0
                && position.getColumn() < columns;
    }

    private static Map<CellType, CellRule> defaultRules() {
        Map<CellType, CellRule> rules = new EnumMap<>(CellType.class);
        rules.put(CellType.CONWAY, new ConwayRule());
        rules.put(CellType.ALTERNATIVE, new AlternativeRule());
        return rules;
    }
}
