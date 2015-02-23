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

import org.orbisgis.sif.components.fstree.TreeNodeCustomIcon;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation for tree node
 * @author Nicolas Fortin
 */
public class GeoCatalogTreeNodeImpl extends DefaultMutableTreeNode implements GeoCatalogTreeNode, TreeNodeCustomIcon {
    private TreeNodeFactory treeNodeFactory;
    private String nodeType;
    private ImageIcon expandedIcon;
    private ImageIcon collapsedIcon;
    private ImageIcon leafIcon;

    public GeoCatalogTreeNodeImpl(TreeNodeFactory treeNodeFactory, String nodeType,String name) {
        super(name);
        this.treeNodeFactory = treeNodeFactory;
        this.nodeType = nodeType;
    }

    public GeoCatalogTreeNodeImpl(TreeNodeFactory treeNodeFactory, String nodeType,String name, ImageIcon expandedIcon, ImageIcon
            collapsedIcon) {
        super(name);
        this.treeNodeFactory = treeNodeFactory;
        this.nodeType = nodeType;
        this.expandedIcon = expandedIcon;
        this.collapsedIcon = collapsedIcon;
    }

    public GeoCatalogTreeNodeImpl(TreeNodeFactory treeNodeFactory, String nodeType,String name, ImageIcon leafIcon) {
        super(name, false);
        this.treeNodeFactory = treeNodeFactory;
        this.nodeType = nodeType;
        this.leafIcon = leafIcon;
    }

    @Override
    public String getNodeType() {
        return nodeType;
    }

    @Override
    public ImageIcon getLeafIcon() {
        return leafIcon;
    }

    @Override
    public ImageIcon getClosedIcon() {
        return collapsedIcon;
    }

    @Override
    public ImageIcon getOpenIcon() {
        return expandedIcon;
    }

    @Override
    public TreeNodeFactory getFactory() {
        return treeNodeFactory;
    }

    @Override
    public String getNodeIdentifier() {
        return (String)getUserObject();
    }

    @Override
    public GeoCatalogTreeNode getParent() {
        return (GeoCatalogTreeNode)super.getParent();
    }

    @Override
    public Map<String, GeoCatalogTreeNode> getChildrenIdentifier() {
        Map<String, GeoCatalogTreeNode> childrenIdent = new HashMap<>();
        if(children == null) {
            return childrenIdent;
        }
        for(Object child : children) {
            if(child instanceof GeoCatalogTreeNode) {
                GeoCatalogTreeNode geoCatalogTreeNode = ((GeoCatalogTreeNode) child);
                childrenIdent.put(geoCatalogTreeNode.getNodeIdentifier(), geoCatalogTreeNode);
            }
        }
        return childrenIdent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoCatalogTreeNodeImpl)) return false;

        GeoCatalogTreeNodeImpl that = (GeoCatalogTreeNodeImpl) o;

        return nodeType.equals(that.nodeType) && getNodeIdentifier().equals(that.getNodeIdentifier());
    }

    @Override
    public int hashCode() {
        return nodeType.hashCode();
    }
}
