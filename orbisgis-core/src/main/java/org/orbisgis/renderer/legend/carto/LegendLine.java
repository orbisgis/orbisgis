package org.orbisgis.renderer.legend.carto;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import org.orbisgis.renderer.Renderer;
import org.orbisgis.renderer.symbol.Symbol;

public class LegendLine {

	private Symbol symbol;
	private String text;

	public LegendLine(Symbol symbol, String text) {
		this.symbol = symbol;
		this.text = text;
	}

	public void drawImage(Graphics g) {
		Renderer renderer = new Renderer();
		renderer.drawSymbolPreview(g, symbol, 30, 20);
		g.setColor(Color.black);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(text, g);
		g.drawString(text, 35, (int) (10 + r.getHeight() / 2));
	}

	public int[] getImageSize(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D stringBounds = fm.getStringBounds(text, g);
		int width = 35 + (int) stringBounds.getWidth();

		return new int[] { width, (int) Math.max(stringBounds.getHeight(), 20) };
	}

}
