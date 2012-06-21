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
package org.orbisgis.core.ui.plugins.views.geocognition;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionListener;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeModel;
import org.orbisgis.core.ui.components.resourceTree.FilterTreeModelDecorator;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.IGeocognitionFilter;

public class GeocognitionTreeModel extends FilterTreeModelDecorator {

	private ArrayList<IGeocognitionFilter> filters = new ArrayList<IGeocognitionFilter>();

	public GeocognitionTreeModel(JTree tree) {
		super(new GecognitionModel(tree), tree);

		Geocognition gc = Services.getService(Geocognition.class);
		gc.addGeocognitionListener(new GeocognitionListener() {

			@Override
			public void elementRemoved(Geocognition geocognition,
					GeocognitionElement element) {
				fireEvent(null);
			}

			@Override
			public void elementAdded(Geocognition geocognition,
					GeocognitionElement parent, GeocognitionElement newElement) {
				fireEvent(null);
			}

			@Override
			public boolean elementRemoving(Geocognition geocognition,
					GeocognitionElement element) {
				return true;
			}

			@Override
			public void elementMoved(Geocognition geocognition,
					GeocognitionElement element, GeocognitionElement oldParent) {
				fireEvent(null);
			}

		});
	}

	public void filter(String text, ArrayList<IGeocognitionFilter> filters) {
		this.filters = filters;
		filter(text.toLowerCase());
		fireEvent(null);
	}

	@Override
	protected boolean isFiltered() {
		return (super.isFiltered() || (!this.filters.isEmpty()));
	}

	@Override
	protected boolean isFiltered(Object elem) {
		GeocognitionElement element = (GeocognitionElement) elem;

		if (super.isFiltered(element)) {
			if (isLeaf(element)) {
				if (!filters.isEmpty()) {
					boolean anyMatches = false;
					for (IGeocognitionFilter filter : filters) {
						if (filter.accept(element.getTypeId())) {
							anyMatches = true;
							break;
						}
					}
					return anyMatches;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private static class GecognitionModel extends AbstractTreeModel {
		private GecognitionModel(JTree tree) {
			super(tree);
		}

		@Override
		public Object getChild(Object parent, int index) {
			GeocognitionElement parentElement = (GeocognitionElement) parent;
			return parentElement.getElement(index);
		}

		@Override
		public int getChildCount(Object parent) {
			GeocognitionElement parentElement = (GeocognitionElement) parent;
			return parentElement.getElementCount();
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			GeocognitionElement parentElement = (GeocognitionElement) parent;
			for (int i = 0; i < parentElement.getElementCount(); i++) {
				if (parentElement.getElement(i) == child) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			Geocognition gc = Services.getService(Geocognition.class);
			return gc.getRoot();
		}

		@Override
		public boolean isLeaf(Object node) {
			GeocognitionElement elem = (GeocognitionElement) node;
			return !elem.isFolder() || elem.getElementCount() == 0;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			// do nothing
		}
	}
}
