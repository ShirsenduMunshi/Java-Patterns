import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class SmoothButterflyCurve extends JPanel implements ActionListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    // Animation and curve parameters
    private static final double INITIAL_T = 0;
    private static final double STEP_INCREMENT = 0.02;
    private static final int TIMER_DELAY_MS = 16; // Approximately 60 FPS
    private static final double CURVE_SCALE = 60; // Scale factor for the butterfly curve

    // Trail rendering parameters
    private static final float TRAIL_FADE_ALPHA = 0.01f;
    private static final float GLOW_ALPHA = 0.1f;
    private static final int GLOW_COLOR_ALPHA = 80;
    private static final float MAIN_STROKE_WIDTH = 2.0f;
    private static final float GLOW_STROKE_WIDTH = 6.0f;


    private double t = INITIAL_T;
    private final Timer timer;

    private final BufferedImage trailCanvas;
    private final Graphics2D trailG;

    // Pre-instantiated composite and stroke objects for efficiency
    private final AlphaComposite fadeComposite;
    private final AlphaComposite drawComposite;
    private final AlphaComposite clearComposite;
    private final BasicStroke mainStroke;
    private final BasicStroke glowStroke;


    private int prevX = -1, prevY = -1;

    public SmoothButterflyCurve() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // Initialize BufferedImage for drawing trails
        trailCanvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        trailG = trailCanvas.createGraphics();
        trailG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pre-instantiate AlphaComposite objects
        fadeComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRAIL_FADE_ALPHA);
        drawComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        clearComposite = AlphaComposite.Clear;

        // Pre-instantiate BasicStroke objects
        mainStroke = new BasicStroke(MAIN_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        glowStroke = new BasicStroke(GLOW_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        trailG.setStroke(mainStroke); // Set initial stroke

        // Initialize and start the timer
        timer = new Timer(TIMER_DELAY_MS, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the accumulated trails onto the panel
        g.drawImage(trailCanvas, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Apply fade effect to the existing trails
        trailG.setComposite(fadeComposite);
        trailG.setColor(Color.BLACK);
        trailG.fillRect(0, 0, WIDTH, HEIGHT);
        // Reset composite for drawing new segments
        trailG.setComposite(drawComposite);

        // Calculate current point on the butterfly curve
        // Butterfly formula: r = e^(cos(t)) - 2cos(4t) + sin^5(t/12)
        // x = r * sin(t), y = r * cos(t)
        double r = Math.exp(Math.cos(t)) - 2 * Math.cos(4 * t) - Math.pow(Math.sin(t / 12), 5);
        double x = Math.sin(t) * r;
        double y = Math.cos(t) * r;

        // Convert polar coordinates to Cartesian screen coordinates
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int px = centerX + (int) (x * CURVE_SCALE);
        int py = centerY - (int) (y * CURVE_SCALE); // Y-axis inverted for Swing (0 at top)

        // Determine color based on current 't' for a rainbow effect
        float hue = (float) (t % (2 * Math.PI)) / (float) (2 * Math.PI);
        Color currentColor = Color.getHSBColor(hue, 1f, 1f);
        trailG.setColor(currentColor);

        // Draw line segments if a previous point exists
        if (prevX != -1 && prevY != -1) {
            // Draw glowing back layer with reduced opacity and thicker stroke
            trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, GLOW_ALPHA));
            trailG.setStroke(glowStroke);
            trailG.setColor(new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), GLOW_COLOR_ALPHA));
            trailG.draw(new Line2D.Double(prevX, prevY, px, py));

            // Draw main bright line on top
            trailG.setComposite(drawComposite); // Reset composite
            trailG.setStroke(mainStroke);
            trailG.setColor(currentColor);
            trailG.draw(new Line2D.Double(prevX, prevY, px, py));
        }

        // Update previous point and increment 't' for the next frame
        prevX = px;
        prevY = py;
        t += STEP_INCREMENT;
        repaint(); // Request a repaint to update the display
    }

    /**
     * Resets the animation by clearing the trail canvas and resetting parameters.
     */
    public void reset() {
        trailG.setComposite(clearComposite);
        trailG.setColor(new Color(0, 0, 0, 0)); // Transparent black
        trailG.fillRect(0, 0, WIDTH, HEIGHT);
        trailG.setComposite(drawComposite); // Reset composite for drawing
        t = INITIAL_T;
        prevX = -1;
        prevY = -1;
        repaint();
    }

    /**
     * Toggles the animation's pause/resume state.
     */
    public void toggle() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🦋 Smooth Butterfly Curve");
            SmoothButterflyCurve panel = new SmoothButterflyCurve();

            // Create and configure the Pause/Resume button
            JButton pauseBtn = new JButton("⏸️ Pause");
            pauseBtn.addActionListener(e -> {
                panel.toggle();
                pauseBtn.setText(panel.timer.isRunning() ? "⏸️ Pause" : "▶️ Resume");
            });

            // Create and configure the Reset button
            JButton resetBtn = new JButton("🔄 Reset");
            resetBtn.addActionListener(e -> panel.reset());

            // Create a panel for controls and add buttons
            JPanel controls = new JPanel();
            controls.add(pauseBtn);
            controls.add(resetBtn);

            // Set up the main frame layout
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER); // Add the animation panel to the center
            frame.add(controls, BorderLayout.SOUTH); // Add controls to the bottom
            frame.pack(); // Size the frame to fit its contents
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
            frame.setLocationRelativeTo(null); // Center the frame on the screen
            frame.setVisible(true); // Make the frame visible
        });
    }
}
