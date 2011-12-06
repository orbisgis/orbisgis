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

import java.io.File;
import junit.framework.TestCase;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.source.SourceManager;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

public abstract class AbstractTest extends TestCase {

        protected FailErrorManager failErrorManager;

        @Override
        public void setUp() throws Exception {
                failErrorManager = new FailErrorManager();
                Services.registerService(ErrorManager.class, "", failErrorManager);
                Services.registerService( OutputManager.class, "output", new ConsoleOutputManager());

                OrbisgisCoreServices.installServices();
        }

        public static void registerDataManager(SQLDataSourceFactory dsf) {
                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));
        }

        public static void registerDataManager() {
                SQLDataSourceFactory dsf = new SQLDataSourceFactory();
                dsf.setResultDir(new File("target/results"));
                dsf.setTempDir("target/results");
                registerDataManager(dsf);
        }

        protected SourceManager getsSourceManager() {
                return getDataManager().getSourceManager();
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
