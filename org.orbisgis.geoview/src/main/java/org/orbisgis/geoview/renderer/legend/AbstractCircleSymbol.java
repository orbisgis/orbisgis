package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;


public abstract class AbstractCircleSymbol extends AbstractPointSymbol {

	private int size;
	private Color outline;
	private Color fillColor;

	public AbstractCircleSymbol(Color outline, Color fillColor, int size) {
		this.size = size;
		this.outline = outline;
		this.fillColor = fillColor;
	}

	protected void paintCircle(Graphics2D g, int x, int y) {
		x = x - size / 2;
		y = y - size / 2;
		if (fillColor != null) {
			g.setPaint(fillColor);
			g.fillOval(x, y, size, size);
		}
		g.setColor(outline);
		g.drawOval(x, y, size, size);

	}
}
