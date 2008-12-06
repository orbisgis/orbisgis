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
package org.orbisgis.views.geocatalog;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.view.IView;
import org.orbisgis.views.geocatalog.newSourceWizard.EPSourceWizardHelper;
import org.orbisgis.views.geocatalog.persistence.ActiveFilter;
import org.orbisgis.views.geocatalog.persistence.Tag;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.workspace.Workspace;

public class CatalogView implements IView {

	private static final String CATALOG_PERSISTENCE_FILE = "org.orbisgis.GeoCatalog.xml";

	private Catalog catalog;

	public CatalogView() {
	}

	public void delete() {
		catalog.delete();
	}

	public Component getComponent() {
		return catalog;
	}

	public void initialize() {
		EPSourceWizardHelper wh = new EPSourceWizardHelper();
		wh.initialize();
		catalog = new Catalog(wh);
	}

	public void loadStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File catalogFile = ws.getFile(CATALOG_PERSISTENCE_FILE);
		if (catalogFile.exists()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(
						"org.orbisgis.views.geocatalog.persistence",
						EPWindowHelper.class.getClassLoader());
				org.orbisgis.views.geocatalog.persistence.Catalog cat = (org.orbisgis.views.geocatalog.persistence.Catalog) jc
						.createUnmarshaller().unmarshal(catalogFile);
				List<ActiveFilter> filters = cat.getActiveFilter();
				ArrayList<String> filterIds = new ArrayList<String>();
				for (ActiveFilter activeFilter : filters) {
					filterIds.add(activeFilter.getId());
				}
				catalog.setActiveFiltersId(filterIds.toArray(new String[0]));

				List<Tag> tags = cat.getTag();
				SourceManager sm = Services.getService(DataManager.class)
						.getSourceManager();
				ArrayList<String> activeLabels = new ArrayList<String>();
				for (Tag tag : tags) {
					catalog.addTag(tag.getText());
					List<String> sources = tag.getSource();
					for (String source : sources) {
						if (sm.exists(source)) {
							catalog.tagSource(tag.getText(), source);
						}
					}

					if (tag.isSelected()) {
						activeLabels.add(tag.getText());
					}
				}
				catalog.setActiveLabels(activeLabels.toArray(new String[0]));
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			}
		}
	}

	public void saveStatus() throws PersistenceException {
		org.orbisgis.views.geocatalog.persistence.Catalog cat = new org.orbisgis.views.geocatalog.persistence.Catalog();
		String[] ids = catalog.getActiveFiltersId();
		for (String filterId : ids) {
			ActiveFilter af = new ActiveFilter();
			af.setId(filterId);
			cat.getActiveFilter().add(af);
		}
		String[] tags = catalog.getTags();
		for (String tag : tags) {
			Tag xmlTag = new Tag();
			xmlTag.setText(tag);
			xmlTag.setSelected(catalog.isTagSelected(tag));
			HashSet<String> sources = catalog.getTaggedSources(tag);
			for (String source : sources) {
				xmlTag.getSource().add(source);
			}
			cat.getTag().add(xmlTag);
		}
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File file = ws.getFile(CATALOG_PERSISTENCE_FILE);
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.views.geocatalog.persistence",
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

}
