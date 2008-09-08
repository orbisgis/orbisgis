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
package org.orbisgis;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.workspace.TestWorkspace;
import org.orbisgis.workspace.Workspace;

public class AbstractTest extends TestCase {

	static {
		Services.registerService("org.orbisgis.Workspace", Workspace.class,
				"",
				new TestWorkspace());
	
		OrbisgisCoreServices.installServices();
	}

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

		public void warning(String userMsg) {
			throw new RuntimeException(userMsg);
		}

	}
}
