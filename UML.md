# UML-klassediagram

Je kunt deze PlantUML-code plakken in een PlantUML-viewer of in IntelliJ met een PlantUML-plugin.

```plantuml
@startuml
skinparam classAttributeIconSize 0

package "nl.aylin.gameoflife.model" {
  enum CellType {
    CONWAY
    ALTERNATIVE
  }

  class Position {
    - int row
    - int column
    + Position(row: int, column: int)
    + getRow(): int
    + getColumn(): int
    + neighborAt(rowOffset: int, columnOffset: int): Position
  }

  class Cell {
    - Position position
    - CellType type
    + Cell(position: Position, type: CellType)
    + getPosition(): Position
    + getType(): CellType
  }

  class GameBoard {
    - int rows
    - int columns
    - Map<CellType, CellRule> rules
    - Map<Position, Cell> cells
    + GameBoard(rows: int, columns: int, rules: Map<CellType, CellRule>)
    + addCell(type: CellType, position: Position): void
    + removeCell(position: Position): void
    + getCell(position: Position): Optional<Cell>
    + countCells(type: CellType): long
    + countLivingNeighbors(position: Position): int
    + nextGeneration(): void
  }
}

package "nl.aylin.gameoflife.rules" {
  interface CellRule {
    + survives(livingNeighbors: int): boolean
    + isBorn(sameTypeNeighbors: int): boolean
  }

  class ConwayRule
  class AlternativeRule
}

package "nl.aylin.gameoflife.clock" {
  interface TickListener {
    + onTick(tickNumber: long): void
  }

  class GameClock {
    - long tickNumber
    - int delayMillis
    - List<TickListener> subscribers
    + start(): void
    + pause(): void
    + resume(): void
    + addSubscriber(listener: TickListener): void
    + removeSubscriber(listener: TickListener): void
    + faster(): void
    + slower(): void
  }
}

package "nl.aylin.gameoflife.controller" {
  enum ToolMode

  class GameController {
    - GameBoard board
    - GameClock clock
    + start(): void
    + pause(): void
    + resume(): void
    + reset(): void
    + placeCell(type: CellType, position: Position): void
    + removeCell(position: Position): void
    + onTick(tickNumber: long): void
  }
}

package "nl.aylin.gameoflife.view" {
  class GameOfLifeFrame
  class GamePanel
}

Cell "1" *-- "1" Position
GameBoard "1" o-- "0..*" Cell
GameBoard "1" o-- "1..*" CellRule
GameBoard ..> Position
CellRule <|.. ConwayRule
CellRule <|.. AlternativeRule
GameClock "1" o-- "0..*" TickListener
GameController ..|> TickListener
GameController --> GameClock
GameController --> GameBoard
GameController ..> Position
GameController ..> CellType
GamePanel --> GameController
GamePanel --> GameBoard
GamePanel ..> ToolMode
GameOfLifeFrame --> GameController
GameOfLifeFrame --> GamePanel

@enduml
```

## Patterns

- Observer pattern: `GameClock` beheert meerdere `TickListener`-subscribers en roept `onTick` aan.
- Strategy pattern: `GameBoard` bewaart per `CellType` een `CellRule`; `ConwayRule` en `AlternativeRule` bepalen polymorf welk gedrag geldt.
- Dependency injection: `GameBoard` kan via de constructor een andere `Map<CellType, CellRule>` krijgen, wat vooral in tests en uitbreidingen handig is.
- MVC: `model` bewaart en berekent de spelstatus, `view` tekent Swing-schermen, en `controller` vertaalt gebruikersacties/ticks naar model-acties.
