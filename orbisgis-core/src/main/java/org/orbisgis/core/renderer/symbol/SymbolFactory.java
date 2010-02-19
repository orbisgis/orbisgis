/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

public class SymbolFactory {

	public static Symbol createPolygonSymbol() {
		return createPolygonSymbol(Color.black);
	}

	public static Symbol createPolygonSymbol(Color outlineColor) {
		return createPolygonSymbol(outlineColor, 1, null);
	}

	public static Symbol createPolygonSymbol(Color outlineColor, Color fillColor) {
		return createPolygonSymbol(outlineColor, 1, fillColor);
	}

	public static Symbol createPolygonSymbol(Color outlineColor, int lineWidth,
			Color fillColor) {
		PolygonSymbol ret = new PolygonSymbol(outlineColor, lineWidth,
				fillColor);

		return ret;
	}

	public static Symbol createPointCircleSymbol(Color outline,
			Color fillColor, int size) {
		return new CirclePointSymbol(outline, 1, fillColor, size, false);
	}

	public static Symbol createPointCircleSymbol(Color outline, int lineWidth,
			Color fillColor, int size, boolean mapUnits) {
		return new CirclePointSymbol(outline, lineWidth, fillColor, size,
				mapUnits);
	}

	public static Symbol createPointSquareSymbol(Color outline, int lineWidth,
			Color fillColor, int size, boolean mapUnits) {
		return new SquarePointSymbol(outline, lineWidth, fillColor, size,
				mapUnits);
	}

	public static Symbol createSymbolComposite(Symbol... symbols) {
		return new SymbolComposite(symbols);
	}

	public static Symbol createLabelSymbol(String text, int fontSize,
			boolean smartPlacing) {
		return new LabelSymbol(text, fontSize, smartPlacing);
	}

	public static Symbol createLineSymbol(Color color, int lineWidth) {
		return new LineSymbol(color, lineWidth);
	}

	public static Symbol createCirclePolygonSymbol(Color outline,
			Color fillColor, int size) {
		return new PolygonCentroidCircleSymbol(outline, 1, fillColor, size,
				false);
	}

	public static Symbol createPolygonCentroidSquareSymbol(Color outline,
			int lineWidth, Color fill, int size, boolean mapUnits) {
		return new PolygonCentroidSquareSymbol(outline, lineWidth, fill, size,
				mapUnits);
	}

	public static Symbol createPolygonCentroidCircleSymbol(Color outline,
			int lineWidth, Color fill, int size, boolean mapUnits) {
		return new PolygonCentroidCircleSymbol(outline, lineWidth, fill, size,
				mapUnits);
	}

	public static Symbol createImageSymbol(URL url) throws IOException {
		ImageSymbol ret = new ImageSymbol();
		ret.setImageURL(url);
		return ret;
	}

	public static Symbol createImageSymbol() {
		ImageSymbol ret = new ImageSymbol();
		try {
			ret.setImageURL(new URL("file:///notexists.png"));
		} catch (IOException e) {
			// ignore
		}
		return ret;
	}

	public static Symbol createVertexCircleSymbol(Color outline, int lineWidth,
			Color fill, int size, boolean mapUnits) {
		return new CircleVertexSymbol(outline, lineWidth, fill, size, mapUnits);
	}

	public static Symbol createVertexSquareSymbol(Color outline, int lineWidth,
			Color fill, int size, boolean mapUnits) {
		return new SquareVertexSymbol(outline, lineWidth, fill, size, mapUnits);
	}

}
