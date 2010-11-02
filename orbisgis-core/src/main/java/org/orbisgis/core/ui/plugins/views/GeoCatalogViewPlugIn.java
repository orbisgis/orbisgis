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
package org.orbisgis.core.ui.plugins.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.geocatalog.persistence.ActiveFilter;
import org.orbisgis.core.ui.geocatalog.persistence.Tag;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.geocatalog.Catalog;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.window.EPWindowHelper;
import org.orbisgis.core.workspace.Workspace;

public class GeoCatalogViewPlugIn extends ViewPlugIn {

	private static final String CATALOG_PERSISTENCE_FILE = "org.orbisgis.core.ui.GeoCatalog.xml";
	private Catalog panel;
	private JMenuItem menuItem;

	public Catalog getPanel() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new Catalog();
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.GEOCATALOG, true,
				OrbisGISIcon.GEOCATALOG_ICON, null, panel, context);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public void loadStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File catalogFile = ws.getFile(CATALOG_PERSISTENCE_FILE);
		if (catalogFile.exists()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(
						"org.orbisgis.core.ui.geocatalog.persistence",
						EPWindowHelper.class.getClassLoader());
				org.orbisgis.core.ui.geocatalog.persistence.Catalog cat = (org.orbisgis.core.ui.geocatalog.persistence.Catalog) jc
						.createUnmarshaller().unmarshal(catalogFile);
				List<ActiveFilter> filters = cat.getActiveFilter();
				ArrayList<String> filterIds = new ArrayList<String>();
				for (ActiveFilter activeFilter : filters) {
					filterIds.add(activeFilter.getId());
				}
				panel.setActiveFiltersId(filterIds.toArray(new String[filterIds.size()]));

				List<Tag> tags = cat.getTag();
				SourceManager sm = Services.getService(DataManager.class)
						.getSourceManager();
				ArrayList<String> activeLabels = new ArrayList<String>();
				for (Tag tag : tags) {
					panel.addTag(tag.getText());
					List<String> sources = tag.getSource();
					for (String source : sources) {
						if (sm.exists(source)) {
							panel.tagSource(tag.getText(), source);
						}
					}

					if (tag.isSelected()) {
						activeLabels.add(tag.getText());
					}
				}
				panel.setActiveLabels(activeLabels.toArray(new String[activeLabels.size()]));
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			}
		}
	}

	public void saveStatus() throws PersistenceException {
		org.orbisgis.core.ui.geocatalog.persistence.Catalog cat = new org.orbisgis.core.ui.geocatalog.persistence.Catalog();
		String[] ids = panel.getActiveFiltersId();
		for (String filterId : ids) {
			ActiveFilter af = new ActiveFilter();
			af.setId(filterId);
			cat.getActiveFilter().add(af);
		}
		String[] tags = panel.getTags();
		for (String tag : tags) {
			Tag xmlTag = new Tag();
			xmlTag.setText(tag);
			xmlTag.setSelected(panel.isTagSelected(tag));
			HashSet<String> sources = panel.getTaggedSources(tag);
			for (String source : sources) {
				xmlTag.getSource().add(source);
			}
			cat.getTag().add(xmlTag);
		}
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File file = ws.getFile(CATALOG_PERSISTENCE_FILE);
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.core.ui.geocatalog.persistence",
					EPWindowHelper.class.getClassLoader());
			PrintWriter printWriter = new PrintWriter(file);
			jc.createMarshaller().marshal(cat, printWriter);
			printWriter.close();
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save geocatalog", e);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Cannot write the file: " + file);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isSelected() {
		boolean isSelected = false;
		isSelected = getPlugInContext().viewIsOpen(getId());
		menuItem.setSelected(isSelected);
		return isSelected;
	}

	public String getName() {
		return "Geocatalog view";
	}
}
