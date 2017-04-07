package org.orbisgis.toolboxeditor.utils;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 * Wait layer displayed on doing an update on the list.
 *
 * @author Sylvain PALOMINOS
 */
public class WaitLayerUI extends LayerUI<JComponent> implements ActionListener {
    private boolean mIsRunning;
    private boolean mIsFadingOut;
    private Timer mTimer;

    private int mAngle;
    private int mFadeCount;
    private int mFadeLimit = 15;
    private static final I18n I18N = I18nFactory.getI18n(WaitLayerUI.class);

    @Override
    public void paint(Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();

        // Paint the view.
        super.paint (g, c);

        if (!mIsRunning) {
            return;
        }

        Graphics2D g2 = (Graphics2D)g.create();

        float fade = (float)mFadeCount / (float)mFadeLimit;
        // Gray it out.
        Composite urComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, .5f * fade));
        g2.fillRect(0, 0, w, h);
        g2.setComposite(urComposite);

        // Paint the wait indicator.
        int s = Math.min(w, h) / 5;
        int cx = w / 2;
        int cy = h / 2;
        g2.setPaint(Color.white);
        //"Loading source" painting
        Font font = g2.getFont().deriveFont(Font.PLAIN, s / 3);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);
        int w1 = metrics.stringWidth(I18N.tr("Loading"));
        int w2 = metrics.stringWidth(I18N.tr("fields"));
        int h1 = metrics.getHeight();
        g2.drawString(I18N.tr("Loading"), cx - w1 / 2, cy - h1 / 2);
        g2.drawString(I18N.tr("source"), cx - w2 / 2, cy + h1 / 2);
        //waiter painting
        g2.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.rotate(Math.PI * mAngle / 180, cx, cy);
        for (int i = 1; i < 12; i++) {
            float scale = (11.0f - (float)i) / 11.0f;
            g2.drawLine(cx + s, cy, cx + s * 2, cy);
            g2.rotate(-Math.PI / 6, cx, cy);
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, scale * fade));
        }
        Toolkit.getDefaultToolkit().sync();
        g2.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        if (mIsRunning) {
            firePropertyChange("tick", 0, 1);
            mAngle += 3;
            if (mAngle >= 360) {
                mAngle = 0;
            }
            if (mIsFadingOut) {
                if (--mFadeCount <= 0) {
                    mIsRunning = false;
                    mTimer.stop();
                }
            }
            else if (mFadeCount < mFadeLimit) {
                mFadeCount++;
            }
        }
    }

    public void start() {
        if (mIsRunning) {
            return;
        }

        // Run a thread for animation.
        mIsRunning = true;
        mIsFadingOut = false;
        mFadeCount = 0;
        int fps = 24;
        int tick = 1000 / fps;
        mTimer = new Timer(tick, this);
        mTimer.start();
    }

    public void stop() {
        mIsFadingOut = true;
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
        if ("tick".equals(pce.getPropertyName())) {
            l.repaint();
        }
    }
}
