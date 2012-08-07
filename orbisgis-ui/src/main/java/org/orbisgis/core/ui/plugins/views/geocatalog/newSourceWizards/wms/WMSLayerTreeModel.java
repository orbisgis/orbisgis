/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms;

import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;

public class WMSLayerTreeModel implements TreeModel {

	private WMSClient client;
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public WMSLayerTreeModel(WMSClient client) {
		this.client = client;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((WMSLayer) parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((WMSLayer) parent).getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((WMSLayer) parent).getChildren().indexOf(child);
	}

	@Override
	public Object getRoot() {
		return client.getRootLayer();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((WMSLayer) node).getChildren().size() == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
