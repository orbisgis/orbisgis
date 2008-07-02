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
package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.symbol.CircleSymbol;
import org.orbisgis.renderer.symbol.LineSymbol;
import org.orbisgis.renderer.symbol.PolygonSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Canvas extends JPanel {

	private Symbol s;

	public Canvas() {
		super();
		s = SymbolFactory.createNullSymbol();
		this.setSize(126, 70);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(2, 2, 123, 67);

		GeometryFactory gf = new GeometryFactory();
		Geometry geom = null;

		try {
			Stroke st = new BasicStroke();
			((Graphics2D) g).setStroke(new BasicStroke(new Float(2.0)));
			g.drawRect(1, 1, 124, 68); // Painting a Rectangle for the
			// presentation and selection

			((Graphics2D) g).setStroke(st);

			geom = gf.createLineString(new Coordinate[] {
					new Coordinate(30, 20), new Coordinate(50, 50),
					new Coordinate(70, 20), new Coordinate(90, 50) });
			paintGeometry(g, geom);
			geom = gf.createPoint(new Coordinate(60, 35));
			paintGeometry(g, geom);
			Coordinate[] coordsP = { new Coordinate(30, 25),
					new Coordinate(90, 25), new Coordinate(90, 45),
					new Coordinate(30, 45), new Coordinate(30, 25) };
			CoordinateArraySequence seqP = new CoordinateArraySequence(coordsP);
			geom = gf.createPolygon(new LinearRing(seqP, gf), null);
			paintGeometry(g, geom);

		} catch (DriverException e) {
			((Graphics2D) g).drawString("Cannot generate preview", 0, 0);
		} catch (NullPointerException e) {
			((Graphics2D) g).drawString("Cannot generate preview: ", 0, 0);
			System.out.println(e.getMessage());
		}
	}

	private void paintGeometry(Graphics g, Geometry geom)
			throws DriverException {
		RenderPermission renderPermission = new RenderPermission() {

			public boolean canDraw(Envelope env) {
				return true;
			}

		};
		if (s.acceptGeometry(geom)) {
			s.draw((Graphics2D) g, geom, new AffineTransform(),
					renderPermission);
		}
	}

	public void setSymbol(Symbol sym) {
		this.s = sym;
		this.repaint();
	}

	/**
	 * Gets the real type of layer of the symbol according to their inheritance
	 * and if it's mixed it will see if all the symbols in it are of the same
	 * type or if is a real mixed type.
	 *
	 * @param sym
	 * @return Integer
	 */
	public Integer getConstraint(Symbol sym) {
		if (sym instanceof LineSymbol) {
			return GeometryConstraint.LINESTRING;
		}
		if (sym instanceof CircleSymbol) {
			return GeometryConstraint.POINT;
		}
		if (sym instanceof PolygonSymbol) {
			return GeometryConstraint.POLYGON;
		}
		if (sym.acceptsChildren()) {
			int symbolCount = sym.getSymbolCount();

			boolean allEquals = true;
			int lastConstraint = 0;
			int actualConstraint = 0;

			if (symbolCount > 0) {
				System.out.println("have more than 0 simbols");
				lastConstraint = getConstraint(sym.getSymbol(0));

				if (symbolCount != 1) {
					for (int i = 1; i < symbolCount; i++) {
						actualConstraint = getConstraint(sym.getSymbol(i));

						if (lastConstraint != actualConstraint) {
							allEquals = false;
							System.out.println("not all equals");
							break;
						}

						lastConstraint = actualConstraint;

					}
				}

				if (allEquals == true) {
					System.out.println("all equals");
					return lastConstraint;
				} else
					return null;

			}

			return null;

		}
		return null;
	}

	public Symbol getSymbol() {
		return s;
	}
}