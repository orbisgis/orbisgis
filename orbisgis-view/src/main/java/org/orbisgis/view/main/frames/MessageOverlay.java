package org.orbisgis.view.main.frames;

import org.orbisgis.view.icons.OrbisGISIcon;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;

/**
 * This component use java7 LayerUI in order to set an overlay message on any component
 * @author Nicolas Fortin
 */
public class MessageOverlay extends LayerUI<Container> implements ImageObserver {
    private static final int INTERPOLATION_MAX = 4;
    private static final float LAYER_OPACITY = 0.75f;
    /** Stop the overlay if the message has not change during this time */
    private static final long MESSAGE_TIMEOUT = 3000;
    /** Border in pixels, on top and bottom of text message */
    private static final int OVERLAY_INNER_BORDER = 2;
    private int interpolationDrawn;
    private boolean running;
    private boolean blackInterpolating;
    private boolean keepRunning;
    private ImageIcon icon;
    private ImageIcon iconInfo;
    private ImageIcon iconError;
    private int interpolationCount;
    private String message = "none";
    private Font messageFont;
    private long lastMessageUpdate = 0;
    public enum MESSAGE_TYPE { INFO, ERROR}

    public MessageOverlay() {
        iconInfo = new ImageIcon(MessageOverlay.class.getResource("info.gif"));
        iconInfo.setImageObserver(this);
        iconError = new ImageIcon(MessageOverlay.class.getResource("error.gif"));
        iconError.setImageObserver(this);
        icon = iconInfo;
        messageFont = new JLabel().getFont().deriveFont(Font.BOLD);
    }

    @Override
    public boolean imageUpdate(Image image, int i, int i2, int i3, int i4, int i5) {
        if (running) {
            firePropertyChange("interpolationCount", null, interpolationCount);
            if (blackInterpolating && interpolationDrawn == interpolationCount) {
                if (--interpolationCount <= 0) {
                    running = false;
                }
            }
            else if (interpolationCount < INTERPOLATION_MAX) {
                if(interpolationDrawn == interpolationCount) {
                    interpolationCount++;
                }
            } else if(interpolationCount == INTERPOLATION_MAX && !keepRunning) {
                blackInterpolating = true;
            }
        }
        return running;
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
        if ("interpolationCount".equals(pce.getPropertyName())) {
            l.repaint();
        }
    }

    @Override
    public void paint (Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();
        int iconHeight = icon.getIconHeight();
        int iconWidth = icon.getIconWidth();
        // Paint the view.
        super.paint (g, c);

        if (!running) {
            return;
        }
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setFont(messageFont);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D textSize = fm.getStringBounds(message, g2);
        float fade = Math.max(0, Math.min(1, (float) interpolationCount / (float) INTERPOLATION_MAX));
        Composite urComposite = g2.getComposite();
        // Set alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, LAYER_OPACITY * fade));
        // Set black background
        int overlayHeight = (int)(Math.ceil(Math.max(textSize.getHeight(),iconHeight) + OVERLAY_INNER_BORDER * 2));
        g2.fillRect(0, h - overlayHeight, w, overlayHeight);
        // Draw gif
        g2.drawImage(icon.getImage(), OVERLAY_INNER_BORDER, (int)( h - (overlayHeight / 2.f) - (iconHeight / 2.f)), this);
        // Draw message
        g2.setColor(Color.WHITE);
        g2.drawString(message, OVERLAY_INNER_BORDER * 2 + iconWidth , (int)( h - (overlayHeight / 2.f) + (textSize.getHeight() / 2.f)));
        g2.setComposite(urComposite);
        g2.dispose();
        interpolationDrawn = interpolationCount;
        if(System.currentTimeMillis() > lastMessageUpdate + MESSAGE_TIMEOUT) {
            stop();
        }
    }

    /**
     * @return True if this component is active
     */
    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (running) {
            return;
        }
        // Run a thread for animation.
        running = true;
        blackInterpolating = false;
        interpolationCount = INTERPOLATION_MAX;
        keepRunning = true;
        firePropertyChange("interpolationCount", null, interpolationCount);
    }

    public void stop() {
        keepRunning = false;
    }

    /**
     * @param message Shown message
     */
    public void setMessage(String message, MESSAGE_TYPE message_type) {
        this.message = message;
        switch (message_type) {
            case ERROR:
                icon = iconError;
                break;
           default:
                icon = iconInfo;
        }
        lastMessageUpdate = System.currentTimeMillis();
        firePropertyChange("interpolationCount", null, interpolationCount);
    }
}