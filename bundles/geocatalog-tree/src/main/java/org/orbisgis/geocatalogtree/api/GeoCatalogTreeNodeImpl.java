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
package org.orbisgis.geocatalogtree.api;

import org.orbisgis.sif.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.sif.components.fstree.TreeNodeCustomLabel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation for tree node
 * @author Nicolas Fortin
 */
public class GeoCatalogTreeNodeImpl extends DefaultMutableTreeNode implements GeoCatalogTreeNode, TreeNodeCustomIcon, TreeNodeCustomLabel {
    private TreeNodeFactory treeNodeFactory;
    private String nodeType;
    private ImageIcon expandedIcon;
    private ImageIcon collapsedIcon;
    private ImageIcon leafIcon;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean sortChildren = true;

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

    /**
     * @param sortChildren New state of ordering of children
     * @return this
     */
    public GeoCatalogTreeNodeImpl setSortChildren(boolean sortChildren) {
        this.sortChildren = sortChildren;
        return this;
    }

    @Override
    public boolean isChildrenSorted() {
        return sortChildren;
    }

    /**
     * @param label Text rendered into the JTree
     * @return This
     */
    public GeoCatalogTreeNodeImpl setLabel(String label) {
        set(PROP_LABEL, label);
        return this;
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

    @Override
    public boolean isEditable() {
        return (Boolean)getAttributeValue(PROP_EDITABLE, false);
    }

    /**
     * @param editable True to set this node editable
     * @return this
     */
    public GeoCatalogTreeNodeImpl setEditable(boolean editable) {
        set(PROP_EDITABLE, editable);
        return this;
    }

    @Override
    public boolean applyCustomLabel(JLabel label) {
        String labelText = (String)getAttributeValue(PROP_LABEL, "");
        if(!labelText.isEmpty()) {
            label.setText(labelText);
        }
        return false;
    }

    @Override
    public Object getAttributeValue(String attributeName) {
        return getAttributeValue(attributeName, null);
    }

    @Override
    public Object getAttributeValue(String attributeName, Object defaultValue) {
        final Object value = attributes.get(attributeName);
        return value == null ? defaultValue : value;
    }

    @Override
    public Set<String> getAttributes() {
        return attributes.keySet();
    }

    @Override
    public GeoCatalogTreeNodeImpl set(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
        return this;
    }
}
