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
package org.orbisgis.view.toc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.view.components.renderers.IconCellRendererUtility;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Toc renderer, try to stick to the Look&Feel but with custom controls.
 */
public class TocRenderer extends TocAbstractRenderer {
        private static Logger UILOGGER = Logger.getLogger("gui."+ TocRenderer.class);
        private static final int ROW_EMPTY_BORDER_SIZE = 2;
        private Rectangle checkBoxRect;
        private MapContext mapContext;

        /**
         * Builds a TocRenderer using the given JTree.
         * @param tree JTree instance
         */
        public TocRenderer(JTree tree) {
                super(tree);
        }

        /**
         * Set the MapContext, used to check if a Layer is Active
         * @param mapContext Map context instance
         */
        public void setMapContext(MapContext mapContext) {
            this.mapContext = mapContext;
        }
        
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
                Component nativeRendererComp = lookAndFeelRenderer.getTreeCellRendererComponent(
                        tree, value, selected, expanded, leaf, row, hasFocus); 
                if(nativeRendererComp instanceof JLabel) {
                        JLabel rendererComponent = (JLabel) nativeRendererComp;
                        try {
                                JPanel panel = new JPanel(new BorderLayout());
                                panel.setOpaque(false);
                                rendererComponent.setBorder(BorderFactory.createEmptyBorder(
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE));
                                JCheckBox checkBox = new JCheckBox();
                                IconCellRendererUtility.copyComponentStyle(rendererComponent,checkBox);
                                if (value instanceof TocTreeNodeLayer) {
                                        ILayer layerNode = ((TocTreeNodeLayer) value).getLayer();
                                        ImageIcon nodeIcon = TocAbstractRenderer.getLayerIcon(layerNode);
                                        if(mapContext!=null && layerNode.equals(mapContext.getActiveLayer())) {
                                            nodeIcon = IconCellRendererUtility.mergeIcons
                                                    (nodeIcon, OrbisGISIcon.getIcon("edition/layer_edit"));
                                        } else if(false) { //!layerNode.getTableReference().isEmpty() && layerNode.getDataSource().isModified()
                                            // TODO Use UndoManager to check for layer modifications
                                            nodeIcon = IconCellRendererUtility.mergeIcons
                                                    (nodeIcon, OrbisGISIcon.getIcon("edition/layer_modify"));
                                        }
                                        rendererComponent.setIcon(nodeIcon);
                                        String nodeLabel = layerNode.getName();
                                        
                                        if(!layerNode.getTableReference().isEmpty() &&
                                                !nodeLabel.equals(layerNode.getTableReference())) {
                                                nodeLabel = I18N.tr("Layer:{0} DataSource :({1})",nodeLabel,layerNode.getTableReference());
                                        }
                                        rendererComponent.setText(nodeLabel);
                                        
                                        
                                        checkBox.setSelected(layerNode.isVisible());
                                } else if(value instanceof TocTreeNodeStyle)  {
                                        Style styleNode = ((TocTreeNodeStyle) value).getStyle();
                                        rendererComponent.setIcon(OrbisGISIcon.getIcon("palette"));
                                        rendererComponent.setText(styleNode.getName());
                                        checkBox.setSelected(styleNode.isVisible());
                                }
                                panel.add(checkBox,BorderLayout.WEST);
                                panel.add(rendererComponent,BorderLayout.CENTER);
                                panel.doLayout();
                                checkBoxRect= new Rectangle(0, 0, checkBox.getPreferredSize().width, checkBox.getPreferredSize().height);
                                rendererComponent.setIcon(
                                        IconCellRendererUtility.mergeComponentAndIcon(
                                                checkBox,rendererComponent.getIcon()));
                        } catch (SQLException | IOException ex) {
                                UILOGGER.error(ex.getLocalizedMessage(), ex);
                        }
                }
                return nativeRendererComp;
	}

	public Rectangle getCheckBoxBounds() {
                if(checkBoxRect!=null) {
                        return checkBoxRect;
                } else {
                        return new Rectangle(0,0);
                }
	}
}
