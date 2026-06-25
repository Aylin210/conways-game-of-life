package nl.aylin.gameoflife.ui;

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
import nl.aylin.gameoflife.clock.GameClock;
import nl.aylin.gameoflife.clock.TickListener;
import nl.aylin.gameoflife.model.CellType;
import nl.aylin.gameoflife.model.GameBoard;

public class GameOfLifeFrame extends JFrame implements TickListener {
    private final GameBoard board = new GameBoard();
    private final GameClock clock = new GameClock();
    private final GamePanel gamePanel = new GamePanel(board);
    private final JLabel tickLabel = new JLabel("Ticks: 0");
    private final JLabel conwayLabel = new JLabel("Conway: 0");
    private final JLabel alternativeLabel = new JLabel("Alternatief: 0");

    public GameOfLifeFrame() {
        super("Conway's Game of Life++");
        // Het venster luistert naar ticks van de GameClock.
        clock.addSubscriber(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(gamePanel), BorderLayout.CENTER);
        gamePanel.setChangeListener(() -> {
            clock.pause();
            updateStatus();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                clock.stop();
            }
        });
        setSize(1000, 750);
        setLocationRelativeTo(null);
        updateStatus();
    }

    @Override
    public void onTick(long tickNumber) {
        // Bij elke tick wordt precies een nieuwe generatie berekend.
        board.nextGeneration();
        SwingUtilities.invokeLater(() -> {
            // GUI-updates horen op de Swing-thread te gebeuren.
            updateStatus();
            gamePanel.repaint();
        });
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<String> modeBox = new JComboBox<>(new String[] {
                "Plaats Conway",
                "Plaats alternatief",
                "Verwijder"
        });
        modeBox.addActionListener(event -> {
            int index = modeBox.getSelectedIndex();
            if (index == 0) {
                gamePanel.setToolMode(ToolMode.PLACE_CONWAY);
            } else if (index == 1) {
                gamePanel.setToolMode(ToolMode.PLACE_ALTERNATIVE);
            } else {
                gamePanel.setToolMode(ToolMode.REMOVE);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(event -> clock.start());

        JButton pauseButton = new JButton("Pauze");
        pauseButton.addActionListener(event -> clock.pause());

        JButton resumeButton = new JButton("Hervat");
        resumeButton.addActionListener(event -> clock.resume());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> {
            // Reset stopt de simulatie, maakt het bord leeg en zet de teller terug.
            clock.stop();
            board.clear();
            updateStatus();
            gamePanel.repaint();
        });

        JButton slowerButton = new JButton("Langzamer");
        slowerButton.addActionListener(event -> clock.slower());

        JButton fasterButton = new JButton("Sneller");
        fasterButton.addActionListener(event -> clock.faster());

        toolbar.add(new JLabel("Actie:"));
        toolbar.add(modeBox);
        toolbar.add(startButton);
        toolbar.add(pauseButton);
        toolbar.add(resumeButton);
        toolbar.add(resetButton);
        toolbar.add(slowerButton);
        toolbar.add(fasterButton);
        toolbar.add(tickLabel);
        toolbar.add(conwayLabel);
        toolbar.add(alternativeLabel);

        return toolbar;
    }

    private void updateStatus() {
        tickLabel.setText("Ticks: " + clock.getTickNumber());
        conwayLabel.setText("Conway: " + board.countCells(CellType.CONWAY));
        alternativeLabel.setText("Alternatief: " + board.countCells(CellType.ALTERNATIVE));
    }
}
