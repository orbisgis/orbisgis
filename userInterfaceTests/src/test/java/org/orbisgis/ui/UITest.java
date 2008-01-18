/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.ui;

import java.io.File;

import junit.framework.TestCase;

import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.core.wizards.OpenGdmsFilePanel;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.EPGeocatalogActionHelper;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceFactory;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.geoview.EPLayerWizardHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.geoview.views.table.Table;
import org.orbisgis.geoview.views.toc.Toc;
import org.orbisgis.pluginManager.Main;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.workspace.WorkspaceFolderFilePanel;
import org.orbisgis.tools.ViewContext;
import org.sif.UIFactory;

/**
 * Base class to test user interface. As some components in the system are
 * static, these tests are sequential and its behavior is based in the status
 * given by the execution of the previous test, so they cannot be run
 * individually
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class UITest extends TestCase {

	protected static Catalog catalog;

	protected static ViewContext viewContext;

	protected static GeoView2D geoview;

	protected static Toc toc;

	protected static Table table;

	protected static SQLConsolePanel sqlConsole;

	static {
		try {
			Main.main(new String[] { "-clean", "-w", "target/workspace", "-p",
					"src/test/resources/plugin-list.xml" });
			PluginManager.setTesting(true);

			UIFactory
					.setPersistencyDirectory(new File("src/test/resources/sif"));

			// Get catalog reference
			initInstances();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static void initInstances() {
		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		catalog = geoCatalog.getCatalog();

		// Get geoview and toc instance
		geoview = (GeoView2D) EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window")[0];
		viewContext = geoview.getViewContext();
		toc = (Toc) geoview.getView("org.orbisgis.geoview.Toc");
		sqlConsole = (SQLConsolePanel) geoview
				.getView("org.orbisgis.geoview.SQLConsole");
		table = (Table) geoview.getView("org.orbisgis.geoview.Table");
	}

	protected void clearCatalog() {
		EPGeocatalogResourceActionHelper.executeAction(catalog,
				"org.orbisgis.geocatalog.ClearCatalog", new IResource[0]);
	}

	protected IResource openFile(String sifInput) {
		return openFile(sifInput, null);
	}

	protected IResource openFile(String sifInput, IResource parent) {
		UIFactory.setInputFor(OpenGdmsFilePanel.OPEN_GDMS_FILE_PANEL, sifInput);
		IResource vectorial = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", parent)[0];
		return vectorial;
	}

	protected ILayer addLayer(String sifInput) {
		UIFactory.setInputFor(OpenGdmsFilePanel.OPEN_GDMS_FILE_PANEL, sifInput);
		return EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard")[0];
	}

	protected IResource createFolder(String folderName)
			throws ResourceTypeException {
		IResource ret = ResourceFactory
				.createResource(folderName, new Folder());
		catalog.getTreeModel().getRoot().addResource(ret);

		return ret;
	}

	protected void saveAndLoad() {
		setWorkspace("test_workspace");
	}

	protected void setWorkspace(String sifInput) {
		UIFactory.setInputFor(WorkspaceFolderFilePanel.SIF_ID, sifInput);
		EPGeocatalogActionHelper.executeAction(catalog,
				"org.orbisgis.geocatalog.ChangeWorkspace");
		initInstances();
		UIFactory.setPersistencyDirectory(new File("src/test/resources/sif"));
	}

}
