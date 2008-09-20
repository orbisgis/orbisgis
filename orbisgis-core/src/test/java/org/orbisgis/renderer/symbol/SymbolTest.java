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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.OrbisgisCoreServices;
import org.orbisgis.Services;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolType;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SymbolTest extends TestCase {

	private final class TestSymbol extends AbstractSymbol {
		@Override
		public String getId() {
			return "org.new.symbol";
		}

		@Override
		public String getClassName() {
			return "Test symbol";
		}

		@Override
		public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
				RenderPermission permission) throws DriverException {
			return null;
		}

		@Override
		public Symbol cloneSymbol() {
			return new TestSymbol();
		}

		@Override
		public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
			return false;
		}

		@Override
		public boolean acceptGeometry(Geometry geom) {
			return false;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OrbisgisCoreServices.installServices();
	}

	public void testManagerClones() throws Exception {
		SymbolManager sv = getSymbolManager();
		Symbol polSym = SymbolFactory.createPolygonSymbol();
		Symbol sym = sv.createSymbol(polSym.getId());
		assertTrue(testEquals(sv.createSymbol(polSym.getId()), sym));
		((EditablePolygonSymbol) sym).setOutlineColor(Color.pink);
		assertTrue(!testEquals(sv.createSymbol(polSym.getId()), sym));
	}

	private SymbolManager getSymbolManager() {
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");
		return sm;
	}

	public void testAvailableSymbolsClone() throws Exception {
		SymbolManager sv = getSymbolManager();
		ArrayList<Symbol> symbols = sv.getAvailableSymbols();
		HashSet<Class<? extends Symbol>> classes = new HashSet<Class<? extends Symbol>>();
		for (Symbol symbol : symbols) {
			Class<? extends Symbol> symbolClass = symbol.getClass();
			assertTrue(!classes.contains(symbolClass));
			classes.add(symbol.getClass());
			assertTrue(testClone(symbol));
		}
	}

	private boolean testClone(Symbol symbol) throws DriverException {
		return testEquals(symbol, symbol.cloneSymbol());
	}

	private boolean testEquals(Symbol symbol1, Symbol symbol2)
			throws DriverException {
		return symbol1.getPersistentProperties().equals(
				symbol2.getPersistentProperties());
	}

	public void testTransparencyPersistence() throws Exception {
		Symbol sym = SymbolFactory.createPointCircleSymbol(new Color(10, 10,
				10, 10), new Color(10, 10, 10, 10), 10);
		Map<String, String> props = sym.getPersistentProperties();
		Symbol sym2 = SymbolFactory.createPointCircleSymbol(new Color(10, 10,
				10, 10), new Color(10, 10, 10, 10), 10);
		sym2.setPersistentProperties(props);

		assertTrue(testEquals(sym, sym2));
	}

	public void testFillPersistence() throws Exception {
		Symbol sym = SymbolFactory.createPolygonSymbol(Color.black);
		testPersistent(sym);
	}

	private Symbol testPersistent(Symbol sym) {
		Symbol sym2 = getSymbolManager().createSymbol(sym.getId());
		sym2.setPersistentProperties(sym.getPersistentProperties());
		assertTrue(sym.getPersistentProperties().equals(
				sym2.getPersistentProperties()));

		return sym2;
	}

	public void testOutlinePersistence() throws Exception {
		Symbol sym = SymbolFactory.createPolygonSymbol(null, Color.black);
		testPersistent(sym);
	}

	public void testMapUnitsPersistence() throws Exception {
		EditablePointSymbol sym = (EditablePointSymbol) SymbolFactory
				.createPointCircleSymbol(Color.black, Color.black, 10);
		sym.setMapUnits(true);
		testPersistent(sym);
		sym.setMapUnits(false);
		assertTrue(!((EditablePointSymbol) testPersistent(sym)).isMapUnits());
	}

	public void testLoadNonExistingSymbol() throws Exception {
		SymbolManager sm = getSymbolManager();
		TestSymbol symbol = new TestSymbol();
		SymbolType obj = sm.getJAXBSymbol(symbol);
		Symbol symbol2 = sm.getSymbolFromJAXB(obj);
		assertTrue(symbol2 == null);
		sm.addSymbol(symbol);
		symbol2 = sm.getSymbolFromJAXB(obj);
		assertTrue(testEquals(symbol, symbol2));
	}

	public void testAlreadyExists() throws Exception {
		try {
			getSymbolManager().addSymbol(SymbolFactory.createPolygonSymbol());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}
}
