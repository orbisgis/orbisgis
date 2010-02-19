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
package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.gdms.source.Source;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class SourceListModel extends AbstractListModel implements ListModel {

	private static final Logger logger = Logger.getLogger(SourceListener.class);

	private String[] names;
	private SyncSourceListener sourceListener;
	private String nameFilter = null;
	private ArrayList<AbstractPlugIn> filters = new ArrayList<AbstractPlugIn>();

	public ArrayList<AbstractPlugIn> getFilters() {
		return filters;
	}

	public SourceListModel() {
		refresh();
		sourceListener = new SyncSourceListener();
		((DataManager) Services.getService(DataManager.class)).getDSF()
				.getSourceManager().addSourceListener(sourceListener);
	}

	private void refresh() {
		DataManager dm = Services.getService(DataManager.class);
		SourceManager sourceManager = dm.getSourceManager();
		String[] names = sourceManager.getSourceNames();
		logger.debug("Showing " + names.length + " sources");
		if (nameFilter != null) {
			AbstractPlugIn textFilter = new TextFilter();
			names = filter(sourceManager, names, textFilter);
		}
		logger.debug("Showing " + names.length + " sources");

		if (filters.size() > 0) {
			names = filter(sourceManager, names, new OrFilter());
		}
		logger.debug("Showing " + names.length + " sources");

		// OULALA
		names = filter(sourceManager, names, new GeocatalogFilter());

		Arrays.sort(names, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		});
		this.names = names;
		fireIntervalRemoved(this, 0, getSize());
		fireIntervalAdded(this, 0, getSize());
	}

	private String[] filter(SourceManager sourceManager, String[] names,
			AbstractPlugIn filter) {
		ArrayList<String> filteredNames = new ArrayList<String>();
		for (String name : names) {
			if (filter.accepts(sourceManager, name)) {
				filteredNames.add(name);
			}
		}
		String[] array = filteredNames.toArray(new String[0]);
		return array;
	}

	@Override
	public Object getElementAt(int index) {
		return names[index];
	}

	@Override
	public int getSize() {
		return names.length;
	}

	public void freeResources() {
		((DataManager) Services.getService(DataManager.class)).getDSF()
				.getSourceManager().removeSourceListener(sourceListener);
	}

	public void filter(String text, ArrayList<AbstractPlugIn> filters) {
		if (text.trim().length() == 0) {
			text = null;
		}
		this.nameFilter = text;
		this.filters = filters;
		refresh();
	}

	private final class SyncSourceListener implements SourceListener {

		public synchronized void sourceRemoved(final SourceRemovalEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Close editors
					closeEditor(e.getName());

					if (e.isWellKnownName()) {
						refresh();
					}
				}
			});
		}

		private void closeEditor(String sourceName) {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor[] editors = em.getEditor(new EditableSource(sourceName));
			for (IEditor editor : editors) {
				em.closeEditor(editor);
			}
		}

		public synchronized void sourceNameChanged(final SourceEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (e.isWellKnownName()) {
						refresh();
					}
				}
			});
		}

		public synchronized void sourceAdded(final SourceEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (e.isWellKnownName()) {
						refresh();
					}
				}
			});
		}
	}

	private final class OrFilter extends AbstractPlugIn {

		@Override
		public boolean accepts(SourceManager sm, String sourceName) {
			for (int i = 0; i < filters.size(); i++) {
				if (filters.get(i).accepts(sm, sourceName)) {
					return true;
				}
			}
			return false;
		}
	}

	private final class TextFilter extends AbstractPlugIn {

		@Override
		public boolean accepts(SourceManager sm, String sourceName) {
			return sourceName.toLowerCase().contains(nameFilter.toLowerCase());
		}
	}
	
	private final class GeocatalogFilter extends AbstractPlugIn {

		@Override
		public boolean accepts(SourceManager sm, String sourceName) {
			Source source = sm.getSource(sourceName);
			return (source != null) && source.isWellKnownName();			
		}
	}
}
