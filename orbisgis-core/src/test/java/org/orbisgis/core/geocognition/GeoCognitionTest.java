package org.orbisgis.core.geocognition;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.gdms.sql.customQuery.RegisterCall;
import org.gdms.sql.function.spatial.geometry.operators.Buffer;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.StandardPolygonSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionListener;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.progress.NullProgressMonitor;

public class GeoCognitionTest extends AbstractGeocognitionTest {

	public void testRootId() throws Exception {
		assertTrue(gc.getRoot().getId().equals(""));
		saveAndLoad();
		assertTrue(gc.getRoot().getId().equals(""));
	}

	public void testSymbolPersistence() throws Exception {
		Symbol symbol = SymbolFactory.createPolygonSymbol();
		gc.addElement("/org/mysymbol", symbol);
		saveAndLoad();
		Symbol symbol2 = gc.getElement("/org/mysymbol", Symbol.class);
		assertTrue(symbol2.getPersistentProperties().equals(
				symbol.getPersistentProperties()));
	}

	public void testNonSupportedElement() throws Exception {
		try {
			gc.addElement("org.wont.add", new GeoCognitionTest());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testNonUniqueId() throws Exception {
		gc.addElement("org.wont.add", SymbolFactory.createPolygonSymbol());
		try {
			gc.addElement("org.wont.add", SymbolFactory.createPolygonSymbol());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			gc.addFolder("org.wont.add");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		gc.addFolder("myfolder");
		gc.addElement("/myfolder/org.wont.add/Buffer", Buffer.class);
		try {
			gc.addElement("/myfolder/org.wont.add/Buffer", Buffer.class);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			gc.addFolder("/myfolder/org.wont.add");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testAddParentDoesNotExist() throws Exception {
		gc.addElement("/it.will.be.created/Buffer", Buffer.class);
		assertTrue(gc.getGeocognitionElement("it.will.be.created") != null);
	}

	public void testFunctionPersistence() throws Exception {
		gc.addElement("/Buffer", Buffer.class);
		saveAndLoad();
		Class<?> classFunction = gc.getElement("/Buffer", Class.class);
		assertTrue(classFunction.getName().equals(Buffer.class.getName()));
	}



	public void testCustomQueryPersistence() throws Exception {
		gc.addElement("/Register", RegisterCall.class);
		saveAndLoad();
		Class<?> classCQ = gc.getElement("/Register", Class.class);
		assertTrue(classCQ.getName().equals(RegisterCall.class.getName()));
	}





	public void testLegendPersistence() throws Exception {
		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
		Symbol symbol = SymbolFactory.createPolygonSymbol(Color.pink);
		legend.setSymbol(symbol);
		UniqueSymbolLegend legend2 = LegendFactory.createUniqueSymbolLegend();
		gc.addElement("org.mylegend", legend);
		gc.addElement("org.mylegend2", legend2);
		saveAndLoad();
		legend = gc.getElement("org.mylegend", UniqueSymbolLegend.class);
		assertTrue(legend.getSymbol().getPersistentProperties().equals(
				symbol.getPersistentProperties()));
		legend2 = gc.getElement("org.mylegend2", UniqueSymbolLegend.class);
		assertTrue(legend2.getSymbol() == null);
	}

	public void testMapContextPersistence() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		ILayer lyr = dm.createLayer(new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(lyr);
		mc.close(null);
		gc.addElement("org.mymap", mc);
		saveAndLoad();
		mc = gc.getElement("org.mymap", MapContext.class);
		mc.open(null);
		assertTrue(mc.getLayerModel().getLayerCount() == 1);
		mc.close(null);
	}

	public void testMapContextIsModifed() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		ILayer lyr = dm.createLayer(new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(lyr);
		mc.getLayerModel().addLayer(dm.createLayerCollection("group"));
		mc.close(null);
		gc.addElement("org.mymap", mc);
		GeocognitionElement elem = gc.getGeocognitionElement("org.mymap");
		elem.open(null);
		assertTrue(!elem.isModified());
		elem.close(null);
	}

	public void testNotFoundSymbolReturnsNull() throws Exception {
		assertTrue(gc.getElement("org.not.exists", Symbol.class) == null);
	}

	public void testListenRemove() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		TestListener listener = new TestListener();
		gc.addGeocognitionListener(listener);
		gc.addElement("org.id1", s);
		gc.removeElement("org.id1");
		assertTrue(listener.removed == 1);
		assertTrue(listener.removing == 1);
		gc.addFolder("org.myfolder");
		gc.addElement("/org.myfolder/org.id1", s);
		gc.addElement("/org.myfolder/org.id2", s);
		gc.removeElement("/org.myfolder/org.id1");
		assertTrue(listener.removed == 2);
		assertTrue(listener.removing == 2);
		gc.removeElement("org.myfolder");
		assertTrue(listener.removed == 3);
		assertTrue(listener.removing == 3);
		assertTrue(gc.getRoot().getElementCount() == 0);
	}

	public void testListenAddRemoveFromFolder() throws Exception {
		TestListener listener = new TestListener();
		gc.addGeocognitionListener(listener);
		gc.addElement("org.id1", SymbolFactory.createPolygonSymbol());
		assertTrue(listener.added == 1);
		gc.getRoot().removeElement("org.id1");
		assertTrue(listener.removed == 1);
		assertTrue(listener.removing == 1);
		gc.getRoot().addElement(gc.createFolder("ppp"));
		assertTrue(listener.added == 2);
	}

	public void testRemovalCancellation() throws Exception {
		TestListener listener = new TestListener();
		gc.addGeocognitionListener(listener);
		gc.addElement("/Buffer", Buffer.class);
		listener.cancel = true;
		assertTrue(gc.removeElement("/Buffer") == null);
	}

	public void testListenAdd() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		TestListener listener = new TestListener();
		gc.addGeocognitionListener(listener);
		gc.addElement("org", s);
		gc.addFolder("org.folder");
		// Two elements are created here
		gc.addElement("/org.another.folder/org.contains", s);
		assertTrue(listener.added == 4);
	}

	public void testListenMoveNotRemovePlusAdd() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		TestListener listener = new TestListener();
		gc.addGeocognitionListener(listener);
		gc.addElement("org", s);
		gc.addFolder("org.folder");
		int added = listener.added;
		int removed = listener.removed;
		int removing = listener.removing;
		gc.move("/org", "/org.folder");
		assertTrue(listener.moved == 1);
		assertTrue(listener.added == added);
		assertTrue(listener.removed == removed);
		assertTrue(listener.removing == removing);
	}

	public void testClear() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		gc.addElement("org", s);
		gc.addFolder("org.folder");
		gc.clear();
		assertTrue(gc.getRoot().getElementCount() == 0);
	}

	public void testOpenSaveCloseMap() throws Exception {
		MapContext mc = new DefaultMapContext();
		gc.addElement("id", mc);
		GeocognitionElement element = gc.getGeocognitionElement("id");
		element.open(new NullProgressMonitor());
		String rootLayerName = "root test layer";
		mc.getLayerModel().setName(rootLayerName);
		element.save();
		element.close(new NullProgressMonitor());
		try {
			mc.getLayerModel();
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		element.open(new NullProgressMonitor());
		assertTrue(mc.getLayerModel().getName().equals(rootLayerName));
		mc.getLayerModel().setName("This will not be saved");
		element.close(new NullProgressMonitor());
		element.open(new NullProgressMonitor());
		assertTrue(mc.getLayerModel().getName().equals(rootLayerName));
		element.close(new NullProgressMonitor());
	}

	public void testOpenSaveCloseLegend() throws Exception {
		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
		gc.addElement("id", legend);
		GeocognitionElement element = gc.getGeocognitionElement("id");
		element.open(new NullProgressMonitor());
		Symbol symbol = SymbolFactory.createPolygonSymbol(Color.pink);
		legend.setSymbol(symbol);
		element.save();
		element.close(new NullProgressMonitor());
		element.open(new NullProgressMonitor());
		assertTrue(legend.getSymbol().getPersistentProperties().equals(
				symbol.getPersistentProperties()));
		legend.setSymbol(null);
		element.close(new NullProgressMonitor());
		element.open(new NullProgressMonitor());
		assertTrue(legend.getSymbol().getPersistentProperties().equals(
				symbol.getPersistentProperties()));
		element.close(new NullProgressMonitor());
	}

	public void testOpenSaveCloseSymbol() throws Exception {
		StandardPolygonSymbol symbol = (StandardPolygonSymbol) SymbolFactory
				.createPolygonSymbol();
		gc.addElement("id", symbol);
		GeocognitionElement element = gc.getGeocognitionElement("id");
		element.open(new NullProgressMonitor());
		symbol.setFillColor(Color.pink);
		element.save();
		element.close(new NullProgressMonitor());
		element.open(new NullProgressMonitor());
		assertTrue(symbol.getFillColor().equals(Color.pink));
		symbol.setFillColor(Color.black);
		element.close(new NullProgressMonitor());
		element.open(new NullProgressMonitor());
		assertTrue(symbol.getFillColor().equals(Color.pink));
		element.close(new NullProgressMonitor());
	}

	public void testOpenSaveCloseBuiltinSQL() throws Exception {
		Class<?> buffer = Buffer.class;
		gc.addElement("/Buffer", buffer);
		unsupportedBuiltInSQLEdition("/Buffer");
		Class<?> register = RegisterCall.class;
		gc.addElement("/register", register);
		unsupportedBuiltInSQLEdition("/register");
	}



	private void unsupportedBuiltInSQLEdition(String id) throws Exception {
		GeocognitionElement element = gc.getGeocognitionElement(id);
		try {
			element.open(new NullProgressMonitor());
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			element.save();
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			element.close(new NullProgressMonitor());
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
	}

	public void testMapContextLoadLayerWithoutSource() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		ILayer layer = getDataManager().createLayer("linestring",
				new File("src/test/resources/linestring.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.close(null);

		gc.addElement("org.map", mc);
		GeocognitionElement mapElement = gc.getGeocognitionElement("org.map");
		getDataManager().getSourceManager().remove("linestring");

		saveAndLoad();
		mapElement = gc.getGeocognitionElement("org.map");
		CountingErrorManager em = new CountingErrorManager();
		ErrorManager previous = Services.getErrorManager();
		Services.setService(ErrorManager.class, em);
		mapElement.open(null);
		Services.setService(ErrorManager.class, previous);
		assertTrue(((MapContext) mapElement.getObject()).getLayerModel()
				.getLayerCount() == 0);
		assertTrue(mapElement.isModified());
		mapElement.close(null);
	}

	public void testImportExport() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		gc.addFolder("org");
		gc.addElement("/org/mysymbol", s);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos, "/org");
		GeocognitionElement tree = gc.createTree(new ByteArrayInputStream(bos
				.toByteArray()));
		gc.addGeocognitionElement("/newFolder", tree);
		GeocognitionElement org = gc.getGeocognitionElement("org");
		GeocognitionElement newFolder = gc.getGeocognitionElement("newFolder");
		assertTrue(org.getElementCount() == newFolder.getElementCount());
	}

	public void testFullPath() throws Exception {
		gc.addFolder("org");
		GeocognitionElement org = gc.getGeocognitionElement("org");
		gc.addFolder(org.getIdPath() + "/orbisgis");
		GeocognitionElement orbisgis = gc
				.getGeocognitionElement("/org/orbisgis");
		gc.addFolder(orbisgis.getIdPath() + "/test");
		GeocognitionElement test = gc
				.getGeocognitionElement("/org/orbisgis/test");
		assertTrue(org.getElementCount() == 1);
		assertTrue(orbisgis.getElementCount() == 1);
		assertTrue(test.getElementCount() == 0);
		assertTrue(org.getIdPath().equals("/org"));
		assertTrue(orbisgis.getIdPath().equals("/org/orbisgis"));
		assertTrue(test.getIdPath().equals("/org/orbisgis/test"));
	}

	public void testMoveError() throws Exception {
		Symbol s = SymbolFactory.createPolygonSymbol();
		gc.addFolder("org");
		gc.addElement("sym", s);
		try {
			gc.move("/org", "/sym");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		} catch (UnsupportedOperationException e) {
		}
		assertTrue(gc.getRoot().getElementCount() == 2);
	}

	public void testMoveSameName() throws Exception {
		gc.addFolder("org");
		gc.addFolder("/org/foo");
		gc.addFolder("foo");
		try {
			gc.move("/foo", "/org");
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
	}

	public void testModifyMapContextXML() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		ILayer lyr = dm.createLayer(new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(lyr);
		mc.close(null);
		gc.addElement("mymap", mc);

		GeocognitionElement element = gc.getGeocognitionElement("mymap");
		String xml = element.getXMLContent();
		MapContext mc2 = new DefaultMapContext();
		gc.addElement("mymap2", mc2);
		GeocognitionElement element2 = gc.getGeocognitionElement("mymap2");
		element2.setXMLContent(xml);

		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 1);
	}



	public void testChangeMapIdConflict() throws Exception {
		gc.addElement("A", new DefaultMapContext());
		gc.addElement("B", new DefaultMapContext());
		try {
			gc.getGeocognitionElement("A").setId("B");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testFixedName() throws Exception {
		gc.addElement("Buffer", Buffer.class);
		try {
			gc.addElement("SuperBuffer", Buffer.class);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			gc.getGeocognitionElement("Buffer").setId("fails");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}



	public void testKeepUnsupportedElement() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		ILayer lyr = dm.createLayer(new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(lyr);
		mc.close(null);
		gc.addElement("org.mymap", mc);
		File temp = new File("target/temp.xml");
		gc.write(new FileOutputStream(temp));

		// Create a geocognition without support for maps
		gc = new DefaultGeocognition();
		DefaultGeocognition.clearFactories();

		failErrorManager.setIgnoreWarnings(true);
		gc.read(new FileInputStream(temp));
		failErrorManager.setIgnoreWarnings(false);
		GeocognitionElement elem = gc.getGeocognitionElement("org.mymap");
		assertTrue(elem != null);
		assertTrue(!(elem.getObject() instanceof MapContext));
		gc.write(new FileOutputStream(temp));

		gc = new DefaultGeocognition();
		gc.addElementFactory(new GeocognitionSymbolFactory());
		gc.addElementFactory(new GeocognitionLegendFactory());
		gc.addElementFactory(new GeocognitionMapContextFactory());
		gc.read(new FileInputStream(temp));
		elem = gc.getGeocognitionElement("org.mymap");
		assertTrue(elem != null);
		assertTrue(new GeocognitionMapContextFactory().acceptContentTypeId(elem
				.getTypeId()));
		MapContext map = gc.getElement("org.mymap", MapContext.class);
		map.open(null);
		assertTrue(map.getLayerModel().getLayerCount() > 0);
		map.close(null);
	}

	private class TestListener implements GeocognitionListener {

		public int moved = 0;
		private int added = 0;
		private int removed = 0;
		private int removing = 0;
		private boolean cancel = false;

		@Override
		public void elementRemoved(Geocognition geocognition,
				GeocognitionElement element) {
			removed++;
		}

		@Override
		public void elementAdded(Geocognition geocognition,
				GeocognitionElement parent, GeocognitionElement newElement) {
			added++;
		}

		@Override
		public boolean elementRemoving(Geocognition geocognition,
				GeocognitionElement element) {
			removing++;
			return !cancel;
		}

		@Override
		public void elementMoved(Geocognition geocognition,
				GeocognitionElement element, GeocognitionElement oldParent) {
			moved++;
		}

	}

	private final class CountingErrorManager implements ErrorManager {
		private int warnings = 0;
		private int errors = 0;

		@Override
		public void warning(String userMsg, Throwable exception) {
			warnings++;
		}

		@Override
		public void warning(String userMsg) {
			warnings++;
		}

		@Override
		public void removeErrorListener(ErrorListener listener) {

		}

		@Override
		public void error(String userMsg, Throwable exception) {
			errors++;
		}

		@Override
		public void error(String userMsg) {
			errors++;
		}

		@Override
		public void addErrorListener(ErrorListener listener) {
		}
	}

}
