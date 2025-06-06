import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class LissajousDrawerWithControls extends JPanel implements ActionListener {

    private int gridSize = 6;
    private static final int CELL_SIZE = 100;
    private static final int MARGIN = CELL_SIZE;
    private int width = MARGIN + gridSize * CELL_SIZE;
    private int height = MARGIN + gridSize * CELL_SIZE;

    private double t = 0;
    private Timer timer;
    private BufferedImage trailCanvas;
    private Graphics2D trailG;

    private Point[][] previousPoints;
    private Color[][] trailColors;

    private int[] xFrequencies;
    private int[] yFrequencies;

    private float trailFadeAlpha = 0.02f;  // Slower fade = longer trail

    public LissajousDrawerWithControls() {
        initGrid(gridSize);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        timer = new Timer(16, this);
        timer.start();
    }

    private void initGrid(int newSize) {
        this.gridSize = newSize;
        this.width = MARGIN + gridSize * CELL_SIZE;
        this.height = MARGIN + gridSize * CELL_SIZE;

        trailCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        trailG = trailCanvas.createGraphics();
        trailG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        trailG.setStroke(new BasicStroke(1.5f));
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        previousPoints = new Point[gridSize][gridSize];
        trailColors = new Color[gridSize][gridSize];

        xFrequencies = new int[gridSize];
        yFrequencies = new int[gridSize];
        for (int i = 0; i < gridSize; i++) {
            xFrequencies[i] = i + 1;
            yFrequencies[i] = i + 1;
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                float hue = (float) (row * gridSize + col) / (gridSize * gridSize);
                trailColors[row][col] = Color.getHSBColor(hue, 1f, 1f);
            }
        }

        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    public void setGridSize(int newSize) {
        initGrid(newSize);
    }

    public void setXFrequency(int index, int value) {
        if (index >= 0 && index < xFrequencies.length) {
            xFrequencies[index] = value;
        }
    }

    public void setYFrequency(int index, int value) {
        if (index >= 0 && index < yFrequencies.length) {
            yFrequencies[index] = value;
        }
    }

    public void clearTrails() {
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        trailG.setColor(new Color(0, 0, 0, 255));
        trailG.fillRect(0, 0, width, height);
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                previousPoints[row][col] = null;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(trailCanvas, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));

        int[] verticalDotX = new int[gridSize];
        int[] horizontalDotY = new int[gridSize];

        for (int i = 0; i < gridSize; i++) {
            verticalDotX[i] = drawGuideCircle(g2, i, true);
            horizontalDotY[i] = drawGuideCircle(g2, i, false);
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int x = verticalDotX[col];
                int y = horizontalDotY[row];
                drawTrailDot(trailG, row, col, x, y);
                drawCurrentDot(g2, x, y);
            }
        }
    }

    private int drawGuideCircle(Graphics2D g2, int index, boolean isTop) {
        int cx = isTop ? MARGIN + index * CELL_SIZE + CELL_SIZE / 2 : MARGIN / 2;
        int cy = isTop ? MARGIN / 2 : MARGIN + index * CELL_SIZE + CELL_SIZE / 2;

        int radius = CELL_SIZE / 2 - 10;
        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        double angle = t * (isTop ? xFrequencies[index] : yFrequencies[index]);
        int dotX = cx + (int) (radius * Math.cos(angle));
        int dotY = cy + (int) (radius * Math.sin(angle));

        g2.setColor(Color.WHITE);
        g2.fillOval(dotX - 4, dotY - 4, 8, 8);

        g2.setColor(new Color(255, 255, 255, 60));
        if (isTop) {
            g2.drawLine(dotX, MARGIN, dotX, height);
            return dotX;
        } else {
            g2.drawLine(MARGIN, dotY, width, dotY);
            return dotY;
        }
    }

    private void drawTrailDot(Graphics2D g2, int row, int col, int x, int y) {
        Point prev = previousPoints[row][col];
        g2.setColor(trailColors[row][col]);
        if (prev != null) {
            g2.drawLine(prev.x, prev.y, x, y);
        }
        previousPoints[row][col] = new Point(x, y);
    }

    private void drawCurrentDot(Graphics2D g2, int x, int y) {
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 3, y - 3, 6, 6);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trailFadeAlpha));
        trailG.setColor(Color.BLACK);
        trailG.fillRect(0, 0, width, height);
        trailG.setComposite(AlphaComposite.SrcOver);
        t += 0.01;
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🎨 Lissajous Art - Controls Enabled");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            LissajousDrawerWithControls panel = new LissajousDrawerWithControls();
            JScrollPane scrollPane = new JScrollPane(panel);

            JPanel controlPanel = new JPanel(new GridLayout(0, 1));
            controlPanel.setBackground(Color.BLACK);

            // Pause/Resume
            JButton toggleBtn = new JButton("⏸️ Pause");
            toggleBtn.addActionListener(e -> {
                if (panel.timer.isRunning()) {
                    panel.timer.stop();
                    toggleBtn.setText("▶️ Resume");
                } else {
                    panel.timer.start();
                    toggleBtn.setText("⏸️ Pause");
                }
            });

            // Reset
            JButton resetBtn = new JButton("🔄 Reset Trails");
            resetBtn.addActionListener(e -> panel.clearTrails());

            controlPanel.add(toggleBtn);
            controlPanel.add(resetBtn);

            // Grid size
            JPanel gridSizePanel = new JPanel();
            gridSizePanel.setBackground(Color.DARK_GRAY);
            JLabel gridLabel = new JLabel("Grid Size:");
            gridLabel.setForeground(Color.WHITE);
            gridSizePanel.add(gridLabel);
            JSpinner gridSizeSpinner = new JSpinner(new SpinnerNumberModel(6, 1, 12, 1));
            gridSizePanel.add(gridSizeSpinner);
            controlPanel.add(gridSizePanel);

            gridSizeSpinner.addChangeListener(e -> {
                int newSize = (int) gridSizeSpinner.getValue();
                panel.setGridSize(newSize);
                frame.pack();
            });

            frame.add(controlPanel, BorderLayout.WEST);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
