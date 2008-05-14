package org.orbisgis;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.pluginManager.error.ErrorListener;
import org.orbisgis.pluginManager.error.ErrorManager;

public class AbstractTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory(
				"src/test/resources/sources", "src/test/resources/temp");

		Services.registerService("org.orbisgis.DataManager", DataManager.class,
				"", new DefaultDataManager(dsf));
		Services.registerService("org.orbisgis.ErrorManager",
				ErrorManager.class, "", new FailErrorManager());
		super.setUp();
	}

	protected DataManager getDataManager() {
		return (DataManager) Services.getService("org.orbisgis.DataManager");
	}

	private class FailErrorManager implements ErrorManager {

		public void addErrorListener(ErrorListener listener) {
		}

		public void error(String userMsg) {
			throw new RuntimeException(userMsg);
		}

		public void error(String userMsg, Throwable exception) {
			throw new RuntimeException(userMsg, exception);
		}

		public void removeErrorListener(ErrorListener listener) {
		}

		public void warning(String userMsg, Throwable exception) {
			throw new RuntimeException(userMsg, exception);
		}

	}
}
