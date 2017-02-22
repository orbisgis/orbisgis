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

import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.resourceTree.TreeSelectionIterable;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Popup menu on Geocatalog tree. The main goal is not to write a new class for each menu item.
 * @author Nicolas Fortin
 */
public class GeoCatalogTreeAction extends DefaultAction {
    private JTree tree;
    private Set<String> nodeFilter = new HashSet<>();
    private List<CheckAttribute> checkAttributeList = new ArrayList<>();

    /**
     * Constructor
     * @param actionId Unique action identifier
     * @param actionLabel Displayed menu item label
     * @param tree DataBase tree
     */
    public GeoCatalogTreeAction(String actionId, String actionLabel, JTree tree) {
        super(actionId, actionLabel);
        this.tree = tree;
    }

    /**
     * Constructor
     * @param actionId Unique action identifier
     * @param actionLabel Displayed menu item label
     * @param icon Leaf icon
     * @param tree DataBase tree
     */
    public GeoCatalogTreeAction(String actionId, String actionLabel, Icon icon, JTree tree) {
        super(actionId, actionLabel, icon);
        this.tree = tree;
    }

    /**
     * Constructor
     * @param actionId Unique action identifier
     * @param actionLabel Displayed menu item label
     * @param icon Leaf icon
     * @param actionListener Call this action listener when the user click on this menu item
     * @param tree DataBase tree
     */
    public GeoCatalogTreeAction(String actionId, String actionLabel, Icon icon, ActionListener actionListener, JTree
            tree) {
        super(actionId, actionLabel, icon, actionListener);
        this.tree = tree;
    }

    /**
     * Constructor
     * @param actionId Unique action identifier
     * @param actionLabel Displayed menu item label
     * @param actionToolTip Displayed menu item tooltip
     * @param icon Leaf icon
     * @param actionListener Call this action listener when the user click on this menu item
     * @param keyStroke Keyboard shortcut
     * @param tree DataBase tree
     */
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

    public TreeSelectionIterable<GeoCatalogTreeNode> getSelectedTreeNodes() {
        TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
        if(paths != null && paths.length > 0) {
            return new TreeSelectionIterable<>(paths, GeoCatalogTreeNode.class);
        } else {
            return new TreeSelectionIterable<>(new TreePath[0], GeoCatalogTreeNode.class);
        }
    }

    @Override
    public boolean isEnabled() {
        boolean foundIncompatible = false;
        boolean hasSelection = false;
        if(!nodeFilter.isEmpty()) {
            for(GeoCatalogTreeNode node : getSelectedTreeNodes()) {
                hasSelection = true;
                if(!nodeFilter.contains(node.getNodeType())) {
                    foundIncompatible = true;
                    break;
                }
                // Check for custom nodes attributes
                for(CheckAttribute checkAttribute : checkAttributeList) {
                    if ((checkAttribute.checkEquals && !checkAttribute.attributeValue.equals(node.getAttributeValue
                            (checkAttribute.getAttributeName()))) || (!checkAttribute.checkEquals && checkAttribute
                            .attributeValue.equals(node.getAttributeValue(checkAttribute.getAttributeName())))) {
                        foundIncompatible = true;
                        break;
                    }
                }
            }
        }
        return super.isEnabled() && (nodeFilter.isEmpty() || (hasSelection && !foundIncompatible));
    }

    public GeoCatalogTreeAction check(String attributeName, Object attributeValue) {
        checkAttributeList.add(new CheckAttribute(attributeName, attributeValue, true));
        return this;
    }
    public GeoCatalogTreeAction checkNot(String attributeName, Object attributeValue) {
        checkAttributeList.add(new CheckAttribute(attributeName, attributeValue, false));
        return this;
    }

    /**
     * Tree associated with the action
     * @return
     */
    public JTree getTree() {
        return tree;
    }

    private static class CheckAttribute {
        private String attributeName;
        private Object attributeValue;
        private boolean checkEquals = true;

        public CheckAttribute(String attributeName, Object attributeValue, boolean checkEquals) {
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
            this.checkEquals = checkEquals;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public Object getAttributeValue() {
            return attributeValue;
        }

        public boolean isCheckEquals() {
            return checkEquals;
        }
    }
}
