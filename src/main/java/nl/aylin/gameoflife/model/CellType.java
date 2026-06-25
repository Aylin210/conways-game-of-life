package nl.aylin.gameoflife.model;

import java.awt.Color;

public enum CellType {
    CONWAY(new Color(40, 105, 210), "Conway"),
    ALTERNATIVE(new Color(214, 83, 72), "Alternatief");

    private final Color color;
    private final String displayName;

    CellType(Color color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public Color getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }
}

