# Conway's Game of Life++

Herkansingsopdracht SE2: een objectgeorienteerde Java-applicatie met twee celtypen, een `GameClock`, Swing GUI en JUnit-tests.

## Starten in IntelliJ

1. Open deze map als Maven-project in IntelliJ.
2. Start `nl.aylin.gameoflife.Main`.
3. Draai de JUnit-tests in `src/test/java`.

Als IntelliJ niet automatisch Maven herkent, kun je alsnog handmatig `src/main/java` als Sources Root en `src/test/java` als Test Sources Root markeren.

Als IntelliJ meldt dat de JDK mist, kies dan:

`File > Project Structure > Project > SDK > Add SDK > JDK`

Selecteer daarna deze map:

`C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.1\jbr`

## Belangrijke ontwerpkeuzes

- Het bord is `1000x1000`, maar gebruikt intern een `Map<Position, Cell>`. Daardoor worden alleen levende cellen opgeslagen.
- Overleven telt alle levende buurcellen, ongeacht celtype.
- Geboorte telt per celtype:
  - Conway-cel: exact 3 Conway-buren.
  - Alternatieve cel: exact 4 alternatieve buren.
- Als op een lege plek beide geboortevoorwaarden tegelijk gelden, krijgt de alternatieve cel voorrang.
- De `GameClock` gebruikt het Observer pattern via `TickListener`.
- De celregels gebruiken het Strategy pattern via `CellRule`.

## Bediening

- Kies bovenin een plaatsmodus: Conway, Alternatief of Verwijderen.
- Linksklik op het speelveld om de gekozen actie uit te voeren.
- Rechtsklik verwijdert altijd een cel.
- Gebruik Start, Pauze, Hervat, Reset, Langzamer en Sneller voor de simulatie.
