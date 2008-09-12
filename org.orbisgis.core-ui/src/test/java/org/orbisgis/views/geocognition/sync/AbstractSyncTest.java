package org.orbisgis.views.geocognition.sync;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.DataManager;
import org.orbisgis.DefaultDataManager;
import org.orbisgis.OrbisgisCoreServices;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.DefaultGeocognition;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.geocognition.symbology.GeocognitionSymbolFactory;

public class AbstractSyncTest extends TestCase {

	private static String local = "node1.geocognition.xml";
	private static String remote = "node2.geocognition.xml";

	protected SyncManager sm;
	protected GeocognitionElement node1, node2;

	public AbstractSyncTest() {
		super();

		DataSourceFactory dsf = new DataSourceFactory(
				"src/test/resources/sources", "src/test/resources/temp");

		OrbisgisCoreServices.installSymbologyServices();
		OrbisgisCoreServices.installGeocognitionService();
		Services.registerService("org.orbisgis.DataManager", DataManager.class,
				"", new DefaultDataManager(dsf));
		Services.registerService("org.orbisgis.ErrorManager",
				ErrorManager.class, "", new ErrorManager() {

					@Override
					public void warning(String userMsg, Throwable exception) {
						throw new RuntimeException(userMsg + ": " + exception);
					}

					@Override
					public void warning(String userMsg) {
						throw new RuntimeException(userMsg);
					}

					@Override
					public void removeErrorListener(ErrorListener listener) {
					}

					@Override
					public void error(String userMsg, Throwable exception) {
						throw new RuntimeException(exception);
					}

					@Override
					public void error(String userMsg) {
						throw new RuntimeException(userMsg);
					}

					@Override
					public void addErrorListener(ErrorListener listener) {
					}

				});

		sm = new SyncManager();
		Geocognition dcm = new DefaultGeocognition();
		dcm.addElementFactory(new GeocognitionSymbolFactory());
		dcm.addElementFactory(new GeocognitionFunctionFactory());
		dcm.addElementFactory(new GeocognitionCustomQueryFactory());
		dcm.addElementFactory(new GeocognitionLegendFactory());
		dcm.addElementFactory(new GeocognitionMapContextFactory());

		Services.setService("org.orbisgis.Geocognition", dcm);

		try {
			node1 = dcm.createTree(TreeCommitTest.class
					.getResourceAsStream(local));
			node2 = dcm.createTree(TreeCommitTest.class
					.getResourceAsStream(remote));
		} catch (PersistenceException e) {
			throw new RuntimeException("Cannot read test file");
		}

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sm.compare(node1, node2, null);
	}

	protected IdPath createPath(String s) {
		String[] path = s.split(":");
		IdPath a = new IdPath();
		a.addLast("");
		a.addLast("root");
		String aux = "root";
		for (int i = 1; i < path.length; i++) {
			aux += "." + path[i];
			a.addLast(aux);
		}

		return a;
	}
}
