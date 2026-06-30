package nl.aylin.gameoflife.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import nl.aylin.gameoflife.controller.GameController;
import nl.aylin.gameoflife.controller.ToolMode;
import nl.aylin.gameoflife.model.CellType;

public class GameOfLifeFrame extends JFrame {
    private final GameController controller = new GameController();
    private final GamePanel gamePanel = new GamePanel(controller);

    // Deze labels laten bovenin zien wat de status is.
    private final JLabel tickLabel = new JLabel("Ticks: 0");
    private final JLabel conwayLabel = new JLabel("Conway: 0");
    private final JLabel alternativeLabel = new JLabel("Alternatief: 0");

    public GameOfLifeFrame() {
        super("Conway's Game of Life++");

        controller.setChangeListener(() -> SwingUtilities.invokeLater(() -> {
            updateStatus();
            gamePanel.repaint();
        }));

        // Basisinstellingen van het venster.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(gamePanel), BorderLayout.CENTER);

        // Als het venster sluit, stoppen we ook de klok.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                controller.stop();
            }
        });
        setSize(1000, 750);
        setLocationRelativeTo(null);
        updateStatus();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Dropdown waarmee je kiest wat een muisklik doet.
        JComboBox<String> modeBox = new JComboBox<>(new String[] {
                "Plaats Conway",
                "Plaats alternatief",
                "Verwijder"
        });
        modeBox.addActionListener(event -> {
            int index = modeBox.getSelectedIndex();
            ToolMode[] modes = ToolMode.values();

            // De volgorde van ToolMode past bij de volgorde van de teksten hierboven.
            gamePanel.setToolMode(modes[index]);
        });

        toolbar.add(new JLabel("Actie:"));
        toolbar.add(modeBox);
        toolbar.add(button("Start", () -> controller.start()));
        toolbar.add(button("Pauze", () -> controller.pause()));
        toolbar.add(button("Hervat", () -> controller.resume()));
        toolbar.add(button("Reset", () -> controller.reset()));
        toolbar.add(button("Langzamer", () -> controller.slower()));
        toolbar.add(button("Sneller", () -> controller.faster()));
        toolbar.add(tickLabel);
        toolbar.add(conwayLabel);
        toolbar.add(alternativeLabel);

        return toolbar;
    }

    private JButton button(String text, Runnable action) {
        // Klein hulpje zodat we niet steeds dezelfde knop-code hoeven te schrijven.
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void updateStatus() {
        // Zet de cijfers bovenin opnieuw goed.
        tickLabel.setText("Ticks: " + controller.getTickNumber());
        conwayLabel.setText("Conway: " + controller.countCells(CellType.CONWAY));
        alternativeLabel.setText("Alternatief: " + controller.countCells(CellType.ALTERNATIVE));
    }
}
