/*
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
package org.orbisgis.geocatalogtree.impl;

import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.beans.PropertyVetoException;

/**
 * Overload DefaultTreeModel in order to manage edition cancel.
 * @author Nicolas Fortin
 */
public class GeoCatalogTreeModel extends DefaultTreeModel {

    public GeoCatalogTreeModel(TreeNode root) {
        super(root);
    }

    public GeoCatalogTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Object comp = path.getLastPathComponent();
        if(comp instanceof GeoCatalogTreeNode) {
            GeoCatalogTreeNode node = (GeoCatalogTreeNode)comp;
            TreeNodeFactory factory = node.getFactory();
            if(factory != null) {
                try {
                    factory.nodeValueVetoableChange(node, (String) newValue);
                } catch (PropertyVetoException ex) {
                    return;
                }
                String oldValue = node.getNodeIdentifier();
                node.setUserObject(newValue);
                this.nodeChanged(node);
                factory.nodeValueChange(node, oldValue, (String)newValue);
            }
        } else {
            super.valueForPathChanged(path, newValue);
        }
    }
}
