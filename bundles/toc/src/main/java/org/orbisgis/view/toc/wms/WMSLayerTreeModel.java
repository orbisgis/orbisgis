/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.toc.wms;

import com.vividsolutions.wms.MapLayer;
import java.util.ArrayList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This class is used to populate a treemodel with the list of layers available
 * on a WMS server.
 * 
* @author Erwan Bocher
 */
public class WMSLayerTreeModel implements TreeModel {

    private MapLayer client;
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    /**
     * The WMSClient gets the informations on the WMS server. It's used to list
     * the name of all layers.
     *
     * @param client
     */
    public WMSLayerTreeModel(MapLayer root) {
        this.client = root;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((MapLayer) parent).getSubLayer(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((MapLayer) parent).numSubLayers();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((MapLayer) parent).getLayerList().indexOf(child);
    }

    @Override
    public Object getRoot() {
        return client;
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((MapLayer) node).getSubLayerList().isEmpty();
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
}
