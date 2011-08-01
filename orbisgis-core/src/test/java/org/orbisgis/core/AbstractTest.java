/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core;

import java.awt.Color;
import java.io.File;
import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.TestWorkspace;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import org.orbisgis.core.workspace.Workspace;

public class AbstractTest extends TestCase {

	protected FailErrorManager failErrorManager;
    protected FailOutputManager failOutputManager;

	@Override
	protected void setUp() throws Exception {
		failErrorManager = new FailErrorManager();
        failOutputManager = new FailOutputManager();

		Services.registerService(ErrorManager.class, "", failErrorManager);
		Services.registerService(OutputManager.class, "", failOutputManager);
		TestWorkspace workspace = new TestWorkspace();
		workspace.setWorkspaceFolder("target");
		Services.registerService(Workspace.class, "", workspace);
		ApplicationInfo applicationInfo = new OrbisGISApplicationInfo();
		Services.registerService(ApplicationInfo.class,
				"Gets information about the application: "
						+ "name, version, etc.", applicationInfo);

		OrbisgisCoreServices.installServices();

	}

	public static void registerDataManager(DataSourceFactory dsf) {
		// Installation of the service
		Services.registerService(DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, "
                        + "either raster or vectorial",
                        new DefaultDataManager(dsf));
	}

	public static void registerDataManager() {
		DataSourceFactory dsf = new DataSourceFactory();
                dsf.setResultDir(new File("target/"));
                dsf.setTempDir("target/");
		registerDataManager(dsf);
	}

	protected SourceManager getsSourceManager() {
		return getDataManager().getSourceManager();
	}

	protected DataManager getDataManager() {
		return (DataManager) Services.getService(DataManager.class);
	}

    protected class FailOutputManager implements OutputManager {

        @Override
        public void print(String out) {
            System.out.print (out);
        }

        @Override
        public void print(String text, Color color) {
            System.out.print(text);
        }

        @Override
        public void println(String out) {
            System.out.println(out);
        }

        @Override
        public void println(String text, Color color) {
            System.out.println(text);
        }

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
