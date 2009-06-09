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
package org.orbisgis.core;

import junit.framework.TestCase;

import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.workspace.TestWorkspace;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.core.workspace.Workspace;

public class AbstractTest extends TestCase {

	protected FailErrorManager failErrorManager;

	@Override
	protected void setUp() throws Exception {
		failErrorManager = new FailErrorManager();
		Services.registerService(ErrorManager.class, "", failErrorManager);
		TestWorkspace workspace = new TestWorkspace();
		workspace.setWorkspaceFolder("target");
		Services.registerService(Workspace.class, "", workspace);
		OrbisgisCoreServices.installServices();
	}

	protected DataManager getDataManager() {
		return (DataManager) Services.getService(DataManager.class);
	}

	protected class FailErrorManager implements ErrorManager {

		private boolean ignoreWarnings;
		private boolean ignoreErrors;

		public void setIgnoreWarnings(boolean ignore) {
			this.ignoreWarnings = ignore;
		}

		public void addErrorListener(ErrorListener listener) {
		}

		public void error(String userMsg) {
			if (!ignoreErrors) {
				throw new RuntimeException(userMsg);
			}
		}

		public void error(String userMsg, Throwable exception) {
			if (!ignoreErrors) {
				throw new RuntimeException(userMsg, exception);
			}
		}

		public void removeErrorListener(ErrorListener listener) {
		}

		public void warning(String userMsg, Throwable exception) {
			if (!ignoreWarnings) {
				throw new RuntimeException(userMsg, exception);
			}
		}

		public void warning(String userMsg) {
			if (!ignoreWarnings) {
				throw new RuntimeException(userMsg);
			}
		}

		public void setIgnoreErrors(boolean b) {
			this.ignoreErrors = b;
		}

	}
}
