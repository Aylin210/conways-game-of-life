package nl.aylin.gameoflife.model;

import java.util.ArrayList;
import java.util.Collections;
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
    // Alleen levende cellen worden opgeslagen. Lege vakjes hoeven geen object te zijn.
    private final Map<Position, Cell> cells = new HashMap<>();
    private final CellFactory cellFactory = new CellFactory();
    private final CellRule conwayRule = new ConwayRule();
    private final CellRule alternativeRule = new AlternativeRule();

    public GameBoard() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public GameBoard(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Het bord moet minimaal 1 rij en 1 kolom hebben.");
        }
        this.rows = rows;
        this.columns = columns;
    }

    public synchronized int getRows() {
        return rows;
    }

    public synchronized int getColumns() {
        return columns;
    }

    public synchronized void addCell(CellType type, Position position) {
        validateInsideBoard(position);
        // Als er al een cel staat, wordt die vervangen door het gekozen type.
        cells.put(position, cellFactory.create(type, position));
    }

    public synchronized void removeCell(Position position) {
        cells.remove(position);
    }

    public synchronized Optional<Cell> getCell(Position position) {
        return Optional.ofNullable(cells.get(position));
    }

    public synchronized boolean isAlive(Position position) {
        return cells.containsKey(position);
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
        // De GUI krijgt een veilige kopie, zodat tekenen en aanpassen elkaar niet storen.
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
        // Nieuwe generatie wordt eerst apart opgebouwd, zodat oude cellen elkaar niet direct aanpassen.
        Map<Position, Cell> nextCells = new HashMap<>();
        for (Position candidate : collectCandidates()) {
            Cell currentCell = cells.get(candidate);
            int livingNeighbors = countNeighbors(candidate, null);

            if (currentCell != null) {
                // Levende cel: controleer met de regels van zijn eigen type of hij overleeft.
                CellRule rule = ruleFor(currentCell.getType());
                if (rule.survives(livingNeighbors)) {
                    nextCells.put(candidate, currentCell.withPosition(candidate));
                }
            } else {
                // Lege plek: controleer of hier een Conway- of alternatieve cel geboren wordt.
                Cell bornCell = createBornCell(candidate);
                if (bornCell != null) {
                    nextCells.put(candidate, bornCell);
                }
            }
        }

        cells.clear();
        cells.putAll(nextCells);
    }

    private Cell createBornCell(Position position) {
        // Bij geboorte tellen we per celtype. Alternatief krijgt voorrang bij een dubbele match.
        int alternativeNeighbors = countNeighbors(position, CellType.ALTERNATIVE);
        if (alternativeRule.isBorn(alternativeNeighbors)) {
            return cellFactory.create(CellType.ALTERNATIVE, position);
        }

        int conwayNeighbors = countNeighbors(position, CellType.CONWAY);
        if (conwayRule.isBorn(conwayNeighbors)) {
            return cellFactory.create(CellType.CONWAY, position);
        }

        return null;
    }

    private CellRule ruleFor(CellType type) {
        return type == CellType.CONWAY ? conwayRule : alternativeRule;
    }

    private Set<Position> collectCandidates() {
        // Alleen levende cellen en hun buren kunnen veranderen in de volgende generatie.
        Set<Position> candidates = new HashSet<>();
        for (Position position : cells.keySet()) {
            candidates.add(position);
            for (Position neighbor : neighborsOf(position)) {
                if (isInsideBoard(neighbor)) {
                    candidates.add(neighbor);
                }
            }
        }
        return candidates;
    }

    private int countNeighbors(Position position, CellType type) {
        int count = 0;
        for (Position neighbor : neighborsOf(position)) {
            Cell cell = cells.get(neighbor);
            // type == null betekent: tel alle levende buren, ongeacht celtype.
            if (cell != null && (type == null || cell.getType() == type)) {
                count++;
            }
        }
        return count;
    }

    private List<Position> neighborsOf(Position position) {
        // Een cel heeft maximaal 8 buren: horizontaal, verticaal en diagonaal.
        List<Position> neighbors = new ArrayList<>(8);
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                if (rowOffset != 0 || columnOffset != 0) {
                    neighbors.add(position.neighborAt(rowOffset, columnOffset));
                }
            }
        }
        return neighbors;
    }

    private void validateInsideBoard(Position position) {
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
}
