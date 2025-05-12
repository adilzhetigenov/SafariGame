package View.Components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class BuildModePanel extends JPanel {
    private JLabel buildModeLabel;
    private Color backgroundColor = new Color(112, 117, 90, 230);
    private Color textColor = new Color(255, 255, 255);
    private int cornerRadius = 10;

    public BuildModePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(5, 15, 5, 15));

        buildModeLabel = new JLabel("Building Mode");
        buildModeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        buildModeLabel.setForeground(textColor);
        buildModeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(buildModeLabel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle background
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 40);
    }
} 