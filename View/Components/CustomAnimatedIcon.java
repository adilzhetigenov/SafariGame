package View.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.geom.RoundRectangle2D;

public class CustomAnimatedIcon extends JButton {
    private ImageIcon originalIcon;
    private boolean hovered = false;
    private boolean pressed = false;
    

    private Color baseColor = new Color(243, 201, 105);   
    private Color hoverColor = new Color(230, 180, 80);
    private Color pressedColor = new Color(200, 150, 60); 
    private Color borderColor = new Color(93, 58, 0);   
    

    private int cornerRadius = 18;  
    private int padding = 10;       
    
    public CustomAnimatedIcon(ImageIcon icon, int width, int height, Runnable action) {
        super();
        this.originalIcon = new ImageIcon(icon.getImage().getScaledInstance(width - (2 * padding), height - (2 * padding), Image.SCALE_SMOOTH));
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        
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
                pressed = false;
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
        
        int width = getWidth();
        int height = getHeight();
        
        
        Color bgColor = baseColor;
        if (pressed) {
            bgColor = pressedColor;
        } else if (hovered) {
            bgColor = hoverColor;
        }
        
        
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        
        
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius - 1, cornerRadius - 1));
        
        
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(3, 3, width - 6, height - 6, cornerRadius - 3, cornerRadius - 3));
        
        
        float brightness = 1.0f;
        if (hovered) brightness = 1.1f;
        if (pressed) brightness = 0.9f;
        
        
        Image iconImg = originalIcon.getImage();
        Image adjustedIcon = applyBrightness(iconImg, brightness);
        int iconX = (width - iconImg.getWidth(null)) / 2;
        int iconY = (height - iconImg.getHeight(null)) / 2;
        g2.drawImage(adjustedIcon, iconX, iconY, null);
        
        g2.dispose();
    }
    
    private Image applyBrightness(Image img, float brightness) {
        
        BufferedImage buffered = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffered.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        
        
        RescaleOp op = new RescaleOp(new float[]{brightness, brightness, brightness, 1f}, new float[4], null);
        return op.filter(buffered, null);
    }
    
    
    public void setBaseColor(Color color) {
        this.baseColor = color;
        repaint();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = color;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
        repaint();
    }
}