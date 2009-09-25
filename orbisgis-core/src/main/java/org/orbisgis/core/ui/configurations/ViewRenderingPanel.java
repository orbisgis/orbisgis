package org.orbisgis.core.ui.configurations;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ViewRenderingPanel extends JPanel {

	float alpha = 1.0f;

	boolean antialiasing = false;

	private AlphaComposite ac;

	public ViewRenderingPanel(String composite_value) {
		if (composite_value != null) {
			this.alpha = new Float(composite_value);
		}
	}

	// Resets the alpha and composite rules with selected items.
	public void changeRule(String a) {
		alpha = Float.valueOf(a).floatValue();
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

		if (antialiasing) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;

		// Creates the buffered image.
		BufferedImage buffImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gbi = buffImg.createGraphics();

		// Clears the previously drawn image.
		g2.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);

		int rectx = w / 4;
		int recty = h / 4;

		// Draws the rectangle and ellipse into the buffered image.
		gbi.setColor(new Color(0.0f, 0.0f, 1.0f, 1.0f));
		gbi.fill(new Rectangle2D.Double(rectx, recty, 150, 100));
		gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
		gbi.setComposite(ac);
		gbi.fill(new Ellipse2D.Double(rectx + rectx / 2, recty + recty / 2,
				100, 80));

		// Draws the buffered image.
		g2.drawImage(buffImg, null, 0, 0);
	}

	public void changeAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
		repaint();

	}

}
