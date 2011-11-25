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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.InitializationException;
import org.gdms.data.WarningListener;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.configuration.DefaultBasicConfiguration;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.DefaultJavaManager;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.JavaManager;
import org.orbisgis.core.workspace.DefaultOGWorkspace;
import org.orbisgis.core.workspace.IOGWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class OrbisgisUIServices {

	private static final String SOURCES_DIR_NAME = "sources";
	private final static Logger logger = Logger
			.getLogger(OrbisgisUIServices.class);

	/**
	 * Installs all the OrbisGIS core services
	 */
	public static void installServices() {
		OrbisgisCoreServices.installServices();

		installApplicationInfoServices();

		// installWorkspaceServices();

		installGeocognitionService();

		try {
			installJavaServices();
		} catch (IOException e) {
			throw new InitializationException("Cannot initialize Java manager",
					e);
		}
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

		SQLDataSourceFactory dsf = new SQLDataSourceFactory(sourcesDir
				.getAbsolutePath(), ews.getTempFolder().getAbsolutePath());
		dsf.setResultDir(ews.getResultsFolder());

		// Pipeline the warnings in gdms to the warning system in the
		// application
		dsf.setWarninglistener(new WarningListener() {

			public void throwWarning(String msg) {
				Services.getService(ErrorManager.class).warning(msg, null);
			}

			public void throwWarning(String msg, Throwable t, Object source) {
				Services.getService(ErrorManager.class).warning(msg, t);
			}

		});

		// Installation of the service
		Services
				.registerService(
						DataManager.class,
						"Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
						new DefaultDataManager(dsf));
	}

	public static void installGeocognitionService() {
		DefaultGeocognition dg = new DefaultGeocognition();
		Services
				.registerService(
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

	public static void installJavaServices() throws IOException {
		HashSet<File> buildPath = new HashSet<File>();
		ClassLoader cl = OrbisgisUIServices.class.getClassLoader();
		while (cl != null) {
			if (cl instanceof URLClassLoader) {
				URLClassLoader loader = (URLClassLoader) cl;
				URL[] urls = loader.getURLs();
				for (URL url : urls) {
					try {
						if (url.getProtocol().equals("file")) {
							File file = new File(url.toURI());
							buildPath.add(file);
						} else {
						}
					} catch (URISyntaxException e) {
						logger.error("Cannot add classpath url: " + url, e);
					}
				}
			}
			cl = cl.getParent();
		}

		DefaultJavaManager javaManager = new DefaultJavaManager();
		Services.registerService(JavaManager.class,
				"Execution of java code and java scripts", javaManager);
		javaManager.addFilesToClassPath(Arrays.asList(buildPath
				.toArray(new File[buildPath.size()])));

	}
}
