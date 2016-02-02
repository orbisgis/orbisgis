/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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
package org.orbisgis.sif.components.fstree;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import org.orbisgis.sif.components.renderers.TreeLaFRenderer;

/**
 * This rendered take the icon of the TreeNodeCustomIcon interface 
 * @author Nicolas Fortin
 */
public class CustomTreeCellRenderer extends TreeLaFRenderer {

        public CustomTreeCellRenderer(JTree tree) {
                super(tree);
        }
        
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
                Component nativeRendererComp = lookAndFeelRenderer.getTreeCellRendererComponent(
                        tree, value, selected, expanded, leaf, row, hasFocus);
                if (nativeRendererComp instanceof JLabel) {
                        JLabel rendererComponent = (JLabel) nativeRendererComp;
                        String toolTipText=null;
                        if(value instanceof AbstractTreeNode) {
                                String nodeToolType = ((AbstractTreeNode)value).getToolTipText();
                                if(nodeToolType!=null && !nodeToolType.isEmpty()) {
                                        toolTipText = nodeToolType;
                                }
                        }
                        rendererComponent.setToolTipText(toolTipText);
                        // Let the node to customise rendering
                        if (value instanceof TreeNodeCustomLabel) {
                                if(((TreeNodeCustomLabel)value).applyCustomLabel(rendererComponent)) {
                                        //Reload the laf renderer to recover the initial state
                                        updateLFRenderer();                                        
                                }
                        }
                        if (value instanceof TreeNodeCustomIcon) {
                                TreeNodeCustomIcon treeNode = (TreeNodeCustomIcon) value;
                                Icon customIcon;
                                if (leaf) {
                                        customIcon = treeNode.getLeafIcon();
                                } else {
                                        if (expanded) {
                                                customIcon = treeNode.getOpenIcon();
                                        } else {
                                                customIcon = treeNode.getClosedIcon();
                                        }
                                }
                                if(customIcon != null)  {
                                        rendererComponent.setIcon(customIcon);
                                }
                        }
                }
                return nativeRendererComp;
        }

}
