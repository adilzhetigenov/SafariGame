package View.Components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class CustomAnimatedButton extends JButton {
    private Color baseColor = new Color(243, 201, 105);   // #F3C969
    private Color hoverColor = new Color(230, 180, 80);
    private Color pressedColor = new Color(200, 150, 60);
    private Color textColor = new Color(93, 58, 0);        // #5D3A00
    private boolean hovered = false;
    private boolean pressed = false;

    public CustomAnimatedButton(String text, int width, int height, Runnable action) {
        super(text);
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        setForeground(textColor);
        setAlignmentX(CENTER_ALIGNMENT);

        addActionListener(e -> action.run());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = pressed ? pressedColor : (hovered ? hoverColor : baseColor);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded corners

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void paintBorder(Graphics g) {
        // No border
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
    }

    @Override
    public boolean isContentAreaFilled() {
        return false;
    }
}
