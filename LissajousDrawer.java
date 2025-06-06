
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class LissajousDrawer extends JPanel implements ActionListener {

    private static final int GRID_SIZE = 6;
    private static final int CELL_SIZE = 100;
    private static final int MARGIN = CELL_SIZE;
    private static final int WIDTH = MARGIN + GRID_SIZE * CELL_SIZE;
    private static final int HEIGHT = MARGIN + GRID_SIZE * CELL_SIZE;

    private double t = 0;
    private final Timer timer;
    private final BufferedImage trailCanvas;
    private final Graphics2D trailG;

    private final Point[][] previousPoints = new Point[GRID_SIZE][GRID_SIZE];

    public LissajousDrawer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        trailCanvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        trailG = trailCanvas.createGraphics();
        trailG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        trailG.setStroke(new BasicStroke(1.2f));
        trailG.setColor(Color.GRAY);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(trailCanvas, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));

        int[] verticalDotX = new int[GRID_SIZE];
        int[] horizontalDotY = new int[GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            verticalDotX[i] = drawGuideCircle(g2, i, true);   // top row
            horizontalDotY[i] = drawGuideCircle(g2, i, false); // left column
        }

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = verticalDotX[col];
                int y = horizontalDotY[row];
                drawDot(g2, row, col, x, y);
            }
        }
    }
    private int drawGuideCircle(Graphics2D g2, int index, boolean isTop) {
        int cx = isTop
                ? MARGIN + index * CELL_SIZE + CELL_SIZE / 2
                : MARGIN / 2;
        int cy = isTop
                ? MARGIN / 2
                : MARGIN + index * CELL_SIZE + CELL_SIZE / 2;

        int radius = CELL_SIZE / 2 - 10;

        // Draw border circle
        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Circular motion (X and Y)
        double angle = t * (index + 1);
        int dotX = cx + (int) (radius * Math.cos(angle));
        int dotY = cy + (int) (radius * Math.sin(angle));

        g2.setColor(Color.WHITE);
        g2.fillOval(dotX - 4, dotY - 4, 8, 8);

        g2.setColor(Color.LIGHT_GRAY);
        if (isTop) {
            g2.drawLine(dotX, MARGIN, dotX, HEIGHT);
            return dotX;
        } else {
            g2.drawLine(MARGIN, dotY, WIDTH, dotY);
            return dotY;
        }
    }

    private void drawDot(Graphics2D g2, int row, int col, int x, int y) {
        Point prev = previousPoints[row][col];
        if (prev != null) {
            trailG.drawLine(prev.x, prev.y, x, y);
        }

        g2.setColor(Color.WHITE);
        g2.fillOval(x - 3, y - 3, 6, 6);

        previousPoints[row][col] = new Point(x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        t += 0.01;
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lissajous Grid Drawer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new LissajousDrawer());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
