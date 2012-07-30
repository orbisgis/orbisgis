/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core;

import java.io.File;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.plugins.GdmsPlugIn;
import org.gdms.plugins.PlugInManagerListener;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.configuration.DefaultBasicConfiguration;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.ui.plugins.views.sqlConsole.language.SQLMetadataManager;
import org.orbisgis.core.workspace.DefaultOGWorkspace;
import org.orbisgis.core.workspace.IOGWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class OrbisgisUIServices {

        private static final String SOURCES_DIR_NAME = "sources";
        private final static Logger logger = Logger.getLogger(OrbisgisUIServices.class);

        /**
         * Installs all the OrbisGIS core services
         */
        public static void installServices() {
                OrbisgisCoreServices.installServices();

                installApplicationInfoServices();

                // installWorkspaceServices();

                installGeocognitionService();
        }

        private static void installApplicationInfoServices() {
                if (Services.getService(ApplicationInfo.class) == null) {
                        Services.registerService(ApplicationInfo.class,
                                "Gets information about the application: "
                                + "name, version, etc.",
                                new OrbisGISApplicationInfo());
                }
        }

        /**
         * Installs services that depend on the workspace such as the
         * {@link DataManager}
         */
        public static void installWorkspaceServices() {
                Workspace workspace = Services.getService(Workspace.class);

                DefaultOGWorkspace defaultOGWorkspace = new DefaultOGWorkspace();
                Services.registerService(IOGWorkspace.class,
                        "Gives access to directories inside the workspace."
                        + " You can use the temporal folder in "
                        + "the workspace through this service. It lets "
                        + "the access to the results folder",
                        defaultOGWorkspace);

                File sourcesDir = workspace.getFile(SOURCES_DIR_NAME);
                if (!sourcesDir.exists()) {
                        sourcesDir.mkdirs();
                }

                IOGWorkspace ews = Services.getService(IOGWorkspace.class);

                DataSourceFactory dsf = new DataSourceFactory(sourcesDir.getAbsolutePath(), ews.getTempFolder().getAbsolutePath(), Main.PLUGIN_DIRECTORY);
                dsf.setResultDir(ews.getResultsFolder());

                dsf.getPlugInManager().registerListener(new PlugInManagerListener() {

                        @Override
                        public boolean pluginLoading(String name) {
                                Splash.updateText("Found plugin " + name);
                                return true;
                        }

                        @Override
                        public void pluginLoaded(GdmsPlugIn p) {
                                Splash.updateText("Initialized plugin " + p.getName() + ", version " + p.getVersion() + '.');
                        }

                        @Override
                        public void pluginUnloading(GdmsPlugIn p) {
                        }
                });
                
                dsf.loadPlugins();

                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));

                // Install SQL Console metadata caching
                final SQLMetadataManager sqlMetadataManager = new SQLMetadataManager();
                sqlMetadataManager.start();
                Services.registerService(SQLMetadataManager.class, "Gets cached metadata for the SQL Console", sqlMetadataManager);

        }

        public static void installGeocognitionService() {
                DefaultGeocognition dg = new DefaultGeocognition();
                Services.registerService(
                        Geocognition.class,
                        "Registry containing all the artifacts produced and shared by the users",
                        dg);
        }

        protected static void installConfigurationService() {
                BasicConfiguration bc = new DefaultBasicConfiguration();
                Services.registerService(BasicConfiguration.class,
                        "Manages the basic configurations (key, value)", bc);
                bc.load();
        }
}
