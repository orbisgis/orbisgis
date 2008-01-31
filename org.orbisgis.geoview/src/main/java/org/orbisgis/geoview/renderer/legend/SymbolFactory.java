package org.orbisgis.geoview.renderer.legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class SymbolFactory {

	public static Symbol createPolygonSymbol() {
		return new PolygonSymbol();
	}

	public static Symbol createPolygonSymbol(Color outlineColor) {
		return createPolygonSymbol(new BasicStroke(), outlineColor, null);
	}

	public static Symbol createPolygonSymbol(Color outlineColor, Color fillColor) {
		return createPolygonSymbol(new BasicStroke(), outlineColor, fillColor);
	}

	public static Symbol createPolygonSymbol(Stroke stroke, Color outlineColor,
			Color fillColor) {
		PolygonSymbol ret = new PolygonSymbol();
		ret.setStroke(stroke);
		ret.setFillColor(fillColor);
		ret.setOutlineColor(outlineColor);

		return ret;
	}

	public static Symbol createNullSymbol() {
		return new NullSymbol();
	}

	public static Symbol createCirclePointSymbol(Color outline,
			Color fillColor, int size) {
		return new CircleSymbol(outline, fillColor, size);
	}

	public static Symbol createSymbolComposite(Symbol... symbols) {
		return new SymbolComposite(symbols);
	}

	public static Symbol createLabelSymbol(String text, int fontSize) {
		return new LabelSymbol(text, fontSize);
	}

	public static Symbol createLineSymbol(Color color, Stroke stroke) {
		return new LineSymbol(color, stroke);
	}

}
