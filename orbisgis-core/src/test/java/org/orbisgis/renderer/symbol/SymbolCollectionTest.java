package org.orbisgis.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class SymbolCollectionTest extends TestCase {

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

	public void testSymbolClone() throws Exception {
		ArrayList<Symbol> symbols = SymbolFactory.getAvailableSymbols();
		for (Symbol symbol : symbols) {
			testClone(symbol);
		}

		Symbol s1 = SymbolFactory.createCirclePointSymbol(Color.black,
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
		testClone(SymbolFactory.createNullSymbol());
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
				assertTrue(symbol1.toString(), img.getRGB(i, j) == img2.getRGB(
						i, j));
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
}
