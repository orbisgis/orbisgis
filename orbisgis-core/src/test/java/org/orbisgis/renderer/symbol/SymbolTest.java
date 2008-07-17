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
package org.orbisgis.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class SymbolTest extends TestCase {

	public void testFileNotExits() throws Exception {
		File file = new File("target/collection.xml");
		file.delete();
		DefaultSymbolCollection sv = new DefaultSymbolCollection(file);
		try {
			sv.loadCollection();
			assertTrue(false);
		} catch (IOException e) {
		}
	}

	public void testCreateEmptySymbolCollection() throws Exception {
		File file = new File("target/collection.xml");
		file.delete();
		DefaultSymbolCollection sv = new DefaultSymbolCollection(file);
		Symbol polSym = SymbolFactory.createPolygonSymbol();
		sv.addSymbol(polSym);
		Symbol sym = sv.getSymbol(0);
		testEquals(polSym, sym);
		sv.saveXML();
		sv = new DefaultSymbolCollection(file);
		sv.loadCollection();
		assertTrue(sv.getSymbolCount() == 1);
		sv.loadCollection();
		assertTrue(sv.getSymbolCount() == 1);
	}

	public void testCollectionClones() throws Exception {
		File file = new File("target/collection.xml");
		file.delete();
		DefaultSymbolCollection sv = new DefaultSymbolCollection(file);
		Symbol polSym = SymbolFactory.createPolygonSymbol();
		sv.addSymbol(polSym);
		Symbol sym = sv.getSymbol(0);
		((EditablePolygonSymbol) polSym).setOutlineColor(Color.red);
		try {
			testEquals(polSym, sym);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		((EditablePolygonSymbol) sym).setOutlineColor(Color.red);
		try {
			testEquals(sv.getSymbol(0), sym);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testSymbolClone() throws Exception {
		ArrayList<Symbol> symbols = SymbolFactory.getAvailableSymbols();
		HashSet<Class<? extends Symbol>> classes = new HashSet<Class<? extends Symbol>>();
		for (Symbol symbol : symbols) {
			Class<? extends Symbol> symbolClass = symbol.getClass();
			assertTrue(!classes.contains(symbolClass));
			classes.add(symbol.getClass());
			testClone(symbol);
		}

		Symbol s1 = SymbolFactory.createPointCircleSymbol(Color.black,
				Color.red, 3);
		testClone(s1);
		Symbol s2 = SymbolFactory.createCirclePolygonSymbol(Color.black,
				Color.red, 3);
		testClone(s2);
		testClone(SymbolFactory.createSquareVertexSymbol(Color.black,
				Color.red, 3));
		testClone(SymbolFactory
				.createSquareVertexSymbol(Color.black, Color.red));
		testClone(SymbolFactory.createCircleVertexSymbol(Color.black,
				Color.red, 3));
		testClone(SymbolFactory
				.createCircleVertexSymbol(Color.black, Color.red));
		testClone(SymbolFactory.createLabelSymbol("hola", 5));

		testClone(SymbolFactory.createLineSymbol(Color.red, 3));
		testClone(SymbolFactory.createPolygonSymbol());
		testClone(SymbolFactory.createPolygonSymbol(Color.black));
		testClone(SymbolFactory.createPolygonSymbol(Color.black, Color.black));
		testClone(SymbolFactory
				.createPolygonSymbol(Color.black, 4, Color.black));
		testClone(SymbolFactory.createSymbolComposite(s1, s2));
	}

	private void testClone(Symbol symbol) throws DriverException {
		testEquals(symbol, symbol.cloneSymbol());
	}

	private void testEquals(Symbol symbol1, Symbol symbol2)
			throws DriverException {
		BufferedImage img = drawSymbol(symbol1);
		BufferedImage img2 = drawSymbol(symbol2);
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if (img.getRGB(i, j) != img2.getRGB(i, j)) {
					throw new IllegalArgumentException(symbol1.toString());
				}
			}
		}

	}

	private BufferedImage drawSymbol(Symbol sym) throws DriverException {
		BufferedImage img = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		GeometryFactory gf = new GeometryFactory();
		LinearRing lr = gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 2, 0), new Coordinate(10, 2, 0),
				new Coordinate(110, 22, 0), new Coordinate(10, 62, 240),
				new Coordinate(0, 2, 0) });
		Polygon geom = gf.createPolygon(lr, new LinearRing[0]);

		sym.draw(g, geom, new AffineTransform(), new RenderPermission() {

			public boolean canDraw(Envelope env) {
				return true;
			}

		});
		return img;
	}

	public void testTransparencyPersistence() throws Exception {
		Symbol sym = SymbolFactory.createCircleVertexSymbol(new Color(10, 10,
				10, 10), new Color(10, 10, 10, 10));
		Map<String, String> props = sym.getPersistentProperties();
		Symbol sym2 = SymbolFactory.createCircleVertexSymbol(new Color(10, 10,
				10, 10), new Color(10, 10, 10, 10));
		sym2.setPersistentProperties(props, sym.getVersion());

		testEquals(sym, sym2);
	}

	public void testFillPersistence() throws Exception {
		Symbol sym = SymbolFactory.createPolygonSymbol(Color.black);
		Symbol sym2 = SymbolFactory.createPolygonSymbol(Color.red, Color.white);
		sym2.setPersistentProperties(sym.getPersistentProperties(), sym
				.getVersion());
		assertTrue(sym.getPersistentProperties().equals(
				sym2.getPersistentProperties()));
	}

	public void testOutlinePersistence() throws Exception {
		Symbol sym = SymbolFactory.createPolygonSymbol(null, Color.black);
		Symbol sym2 = SymbolFactory.createPolygonSymbol(Color.red, Color.white);
		sym2.setPersistentProperties(sym.getPersistentProperties(), sym
				.getVersion());
		assertTrue(sym.getPersistentProperties().equals(
				sym2.getPersistentProperties()));
	}

}
