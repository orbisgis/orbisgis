package org.orbisgis.orbistoolbox.view.utils.editor.log;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Sylvain PALOMINOS
 */
public class LogLayerUI extends LayerUI<JComponent> implements ActionListener {
    private boolean running;
    private boolean fadingOut;

    private final int countLimit = 15;
    private int count;
    private int angle;

    private Timer timer;

    public void start() {
        if (!running) {
            running = true;
            fadingOut = false;
            count = 0;
            timer = new Timer(1000/24, this);
            timer.start();
        }
    }

    public void stop() {
        fadingOut = true;
    }

    @Override
    public void paint (Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();

        // Paint the view.
        super.paint (g, c);

        if (!running) {
            return;
        }

        Graphics2D g2 = (Graphics2D)g.create();
        float fade = (float) count / (float) countLimit;
        // Gray it out.
        Composite urComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f * fade));
        g2.fillRect(0, 0, w, h);
        //g2.setComposite(urComposite);

        // Paint the wait indicator.
        int s = Math.min(w, h) / 5;
        int cx = w / 2;
        int cy = h / 2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(
                new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(Color.white);
        g2.rotate(Math.PI * angle / 180, cx, cy);
        for (int i = 0; i < 12; i++) {
            float scale = (11.0f - (float)i) / 11.0f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale * fade));
            g2.drawLine(cx + s, cy, cx + s * 2, cy);
            g2.rotate(-Math.PI / 6, cx, cy);
        }
        Toolkit.getDefaultToolkit().sync();
        g2.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        if (running) {
            if (fadingOut) {
                if (count == 0) {
                    running = false;
                    timer.stop();
                }
                else
                    count--;
            }
            else if (count < countLimit) {
                count++;
            }

            angle += 3;
            angle %=360;

            firePropertyChange("tick", 0, 1);
        }
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
        if ("tick".equals(pce.getPropertyName())) {
            l.repaint();
        }
    }
}
