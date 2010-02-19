package org.orbisgis.core.geocognition;

import java.awt.Color;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.generic.GenericObjectDriver;
import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.renderer.legend.Legend;
import org.orbisgis.plugins.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.plugins.core.renderer.symbol.Symbol;
import org.orbisgis.plugins.core.renderer.symbol.SymbolFactory;

public class GeocognitionUpgradeTest extends AbstractGeocognitionTest {

	public void testUnversioned() throws Exception {
		DataManager dm = Services.getService(DataManager.class);
		GenericObjectDriver omd = new GenericObjectDriver(
				new String[] { "the_geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });
		dm.getSourceManager().register("source", omd);

		failErrorManager.setIgnoreWarnings(true);
		gc.read(this.getClass().getResourceAsStream(
				"/org/orbisgis/geocognition/unversioned-geocognition.xml"));
		failErrorManager.setIgnoreWarnings(false);
		MapContext mapContext = (MapContext) gc.getGeocognitionElement(
				"/OrbisGIS/Maps/FirstMap").getObject();
		mapContext.open(null);
		assertTrue(mapContext.getLayerModel().getLayer(0).getRenderingLegend().length > 0);
		mapContext.close(null);

		assertTrue(gc.getGeocognitionElement("/CheckSpatialEquivalence")
				.getObject() == null);

		Symbol symbol0 = gc.getElement("/Symbol0", Symbol.class);
		Symbol testSymbol = SymbolFactory.createPolygonCentroidSquareSymbol(
				Color.black, 1, Color.black, 21, true);
		assertTrue(symbol0.getId().equals(testSymbol.getId()));

		Symbol symbol1 = gc.getElement("/Symbol1", Symbol.class);
		testSymbol = SymbolFactory.createPolygonCentroidCircleSymbol(
				Color.black, 1, Color.black, 21, true);
		assertTrue(symbol1.getId().equals(testSymbol.getId()));

		Legend legend0 = gc.getElement("/Legend0", Legend.class);
		assertTrue(((UniqueSymbolLegend) legend0).getSymbol() == null);
	}
}
