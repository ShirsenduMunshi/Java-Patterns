import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ButterflyCurve extends JPanel implements ActionListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private double t = 0;
    private final double step = 0.02;
    private final Timer timer;

    private final BufferedImage trailCanvas;
    private final Graphics2D trailG;

    private final float trailFadeAlpha = 0.01f; // Low value = long-lasting trail

    public ButterflyCurve() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        trailCanvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        trailG = trailCanvas.createGraphics();
        trailG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        trailG.setStroke(new BasicStroke(2.0f));

        timer = new Timer(16, this); // ~60fps
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(trailCanvas, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Fade effect
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trailFadeAlpha));
        trailG.setColor(Color.BLACK);
        trailG.fillRect(0, 0, WIDTH, HEIGHT);
        trailG.setComposite(AlphaComposite.SrcOver);

        // Butterfly equation
        double scale = 60;
        double x = Math.sin(t) * (Math.exp(Math.cos(t)) - 2 * Math.cos(4 * t) - Math.pow(Math.sin(t / 12), 5));
        double y = Math.cos(t) * (Math.exp(Math.cos(t)) - 2 * Math.cos(4 * t) - Math.pow(Math.sin(t / 12), 5));

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int px = centerX + (int) (x * scale);
        int py = centerY - (int) (y * scale); // Invert Y for traditional coordinate

        // Colorful trail based on t
        float hue = (float) (t % (2 * Math.PI)) / (float) (2 * Math.PI);
        trailG.setColor(Color.getHSBColor(hue, 1f, 1f));
        trailG.fillOval(px, py, 4, 4);

        t += step;
        repaint();
    }

    public void reset() {
        trailG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        trailG.setColor(Color.BLACK);
        trailG.fillRect(0, 0, WIDTH, HEIGHT);
        trailG.setComposite(AlphaComposite.SrcOver);
        t = 0;
        repaint();
    }

    public void toggle() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🦋 Butterfly Curve Animation");
            ButterflyCurve panel = new ButterflyCurve();

            // Controls
            JButton pauseBtn = new JButton("⏸️ Pause");
            pauseBtn.addActionListener(e -> {
                panel.toggle();
                pauseBtn.setText(panel.timer.isRunning() ? "⏸️ Pause" : "▶️ Resume");
            });

            JButton resetBtn = new JButton("🔄 Reset");
            resetBtn.addActionListener(e -> panel.reset());

            JPanel controls = new JPanel();
            controls.add(pauseBtn);
            controls.add(resetBtn);

            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


