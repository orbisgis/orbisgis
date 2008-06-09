package org.orbisgis.renderer.legend;

import java.awt.BasicStroke;
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
		g.setStroke(new BasicStroke(1));
		g.setColor(outline);
		g.drawOval(x, y, size, size);

	}

	public Color getOutlineColor(){
		return outline;
	}

	public Color getFillColor(){
		return fillColor;
	}

	public int getSize(){
		return size;
	}
}
