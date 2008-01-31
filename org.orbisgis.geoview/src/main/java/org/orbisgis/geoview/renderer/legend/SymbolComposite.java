package org.orbisgis.geoview.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;

public class SymbolComposite implements Symbol {

	private Symbol[] symbols;

	public SymbolComposite(Symbol[] symbols) {
		this.symbols = symbols;
	}

	public void draw(Graphics2D g, Geometry geom, AffineTransform at)
			throws DriverException {
		for (Symbol symbol : symbols) {
			symbol.draw(g, geom, at);
		}
	}

	public boolean willDraw(Geometry geom) {
		for (Symbol symbol : symbols) {
			if (symbol.willDraw(geom)) {
				return true;
			}
		}
		return false;
	}

	public int getSymbolCount() {
		return symbols.length;
	}

	public Symbol getSymbol(int i) {
		return symbols[i];
	}

}
