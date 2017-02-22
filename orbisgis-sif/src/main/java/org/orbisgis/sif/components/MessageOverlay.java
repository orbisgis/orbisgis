/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sif.components;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;

/**
 * This component use java7 LayerUI in order to set an overlay message on any component
 * @author Nicolas Fortin
 */
public class MessageOverlay extends LayerUI<Container> implements ImageObserver {
    private final static int DEFAULT_MAX_LENGTH = 120;
    private final int max_length;
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
    private Rectangle2D cachedTextSize = null;

    public MessageOverlay(int maxLength) {
        max_length = maxLength;
        iconInfo = new ImageIcon(MessageOverlay.class.getResource("info.gif"));
        iconInfo.setImageObserver(this);
        iconError = new ImageIcon(MessageOverlay.class.getResource("error.gif"));
        iconError.setImageObserver(this);
        icon = iconInfo;
        messageFont = new JLabel().getFont().deriveFont(Font.BOLD);
    }
    public MessageOverlay() {
        this(DEFAULT_MAX_LENGTH);
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
        // Paint the view.
        super.paint (g, c);
        if (!running) {
            return;
        }
        int w = c.getWidth();
        int h = c.getHeight();
        int iconHeight = icon.getIconHeight();
        int iconWidth = icon.getIconWidth();
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setFont(messageFont);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D textSize = cachedTextSize;
        if(textSize == null) {
            cachedTextSize = fm.getStringBounds(message, g2);
            textSize = cachedTextSize;
        }
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
        this.message = message.substring(0, Math.min(message.length(),max_length));
        cachedTextSize = null;
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
