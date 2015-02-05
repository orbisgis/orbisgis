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
package org.orbisgis.geocatalogtree.api;

import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.resourceTree.TreeSelectionIterable;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Popup menu on Geocatalog tree.
 * @author Nicolas Fortin
 */
public abstract class GeoCatalogTreeAction extends DefaultAction {
    private JTree tree;
    private Set<String> nodeFilter = new HashSet<>();

    public GeoCatalogTreeAction(String actionId, String actionLabel, JTree tree) {
        super(actionId, actionLabel);
        this.tree = tree;
    }

    public GeoCatalogTreeAction(String actionId, String actionLabel, Icon icon, JTree tree) {
        super(actionId, actionLabel, icon);
        this.tree = tree;
    }

    public GeoCatalogTreeAction(String actionId, String actionLabel, Icon icon, ActionListener actionListener, JTree
            tree) {
        super(actionId, actionLabel, icon, actionListener);
        this.tree = tree;
    }

    public GeoCatalogTreeAction(String actionId, String actionLabel, String actionToolTip, Icon icon, ActionListener
            actionListener, KeyStroke keyStroke, JTree tree) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, keyStroke);
        this.tree = tree;
    }

    /**
     * @param nodeType Node type {@link GeoCatalogTreeNode#getNodeType()}
     * @return This
     */
    public GeoCatalogTreeAction addNodeTypeFilter(String... nodeType) {
        nodeFilter.addAll(Arrays.asList(nodeType));
        return this;
    }

    @Override
    public boolean isEnabled() {
        TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
        boolean foundIncompatible = false;
        if(paths != null && paths.length > 0 && !nodeFilter.isEmpty()) {
            TreeSelectionIterable<GeoCatalogTreeNode> selection = new TreeSelectionIterable<>(paths,
                    GeoCatalogTreeNode.class);
            for(GeoCatalogTreeNode node : selection) {
                if(!nodeFilter.contains(node.getNodeType())) {
                    foundIncompatible = true;
                }
            }
        }
        return super.isEnabled() && (nodeFilter.isEmpty() || !foundIncompatible);
    }
}
