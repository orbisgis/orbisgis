package org.orbisgis.core.ui.components.job;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class GradientProgressBarUI extends BasicProgressBarUI {
	
	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		// for antialiasing geometric shapes
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// for antialiasing text
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// to go for quality over speed
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		
		super.paint(g, c);
	}

	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#paintDeterminate(java.awt.Graphics, javax.swing.JComponent)
	 */
	protected void paintDeterminate(Graphics g, JComponent c) {
		if (progressBar.getOrientation() == JProgressBar.VERTICAL) {
			super.paintDeterminate(g, c);
			return;
		}
		Insets b = progressBar.getInsets(); // area for border
		int width = progressBar.getWidth();
		int height = progressBar.getHeight();
		int barRectWidth = width - (b.right + b.left);
		int barRectHeight = height - (b.top + b.bottom);
		int arcSize = height / 2 - 1;
		// amount of progress to draw
		int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(progressBar.getBackground());
		g2.fillRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);

		// Set the gradient fill
		Color color = progressBar.getForeground();
		GradientPaint gradient = new GradientPaint(width / 2, 0, Color.white,
				width / 2, height / 4, color, false);
		g2.setPaint(gradient);

		if(amountFull<2)
			g2.fillRoundRect(b.left, b.top, 1, barRectHeight - 1,
					arcSize, arcSize);
		else
			g2.fillRoundRect(b.left, b.top, amountFull - 1, barRectHeight - 1,
					arcSize, arcSize);

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			paintString(g, b.left, b.top, barRectWidth, barRectHeight,
					amountFull, b);
		}
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#paintIndeterminate(java.awt.Graphics, javax.swing.JComponent)
	 */
	protected void paintIndeterminate(Graphics g, JComponent c) {
		if (progressBar.getOrientation() == JProgressBar.VERTICAL) {
			super.paintDeterminate(g, c);
			return;
		}
		
		int arcSize = progressBar.getHeight() / 2 - 1;
		Graphics2D g2 = (Graphics2D) g;
		boxRect = getBox(boxRect);
		
		if (boxRect != null) {
			g2.setColor(progressBar.getForeground());
			g2.fillRoundRect(boxRect.x, boxRect.y, boxRect.width, boxRect.height, arcSize, arcSize);//Go and come bar
		}
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension dim = super.getPreferredSize(c);
		if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
			if (dim.width < dim.height * 4)
				dim.width = dim.height * 4;
		}
		return dim;
	}

}
