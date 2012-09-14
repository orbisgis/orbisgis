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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Style;

/**
 * Toc renderer, try to stick to the Look&Feel but with custom controls.
 */
public class TocRenderer extends TocAbstractRenderer {
        private static Logger UILOGGER = Logger.getLogger("gui."+ TocRenderer.class);
        private static final long serialVersionUID = 1L;
        private static final int ROW_EMPTY_BORDER_SIZE = 2;
        private Rectangle checkBoxRect;

        /**
         * Builds a TocRenderer using the given JTree.
         * @param tree
         */
        public TocRenderer(JTree tree) {
                super(tree);
        }

        private void copyComponentStyle(JComponent source, JComponent destination) {
                destination.setOpaque(source.isOpaque());
                destination.setBackground(source.getBackground());
                destination.setForeground(source.getForeground());
                destination.setBorder(source.getBorder());
        }
            
        /**
         * Draw the component at the left of the provided icon
         * @param component
         * @param icon
         * @return 
         */
        private static Icon mergeComponentAndIcon(JComponent component,Icon icon) {
                Dimension compSize = component.getPreferredSize();
                component.setSize(compSize);                
                int compWidth = compSize.width;
                int compHeight =  compSize.height;
                int iconY = 0;
                if(icon!=null) {
                        compWidth += icon.getIconWidth();
                        //Set vertical icon alignement to center
                        if(compHeight>icon.getIconHeight()) {
                                iconY = (int)Math.ceil((compHeight - icon.getIconHeight()) / 2);
                        } else {
                                compHeight = icon.getIconHeight();
                        }
                }
                //Create an icon that is the copound of the checkbox with the row icon
                if(compWidth<=0 || compHeight<=0) {
                        return null;
                }
                BufferedImage image = new BufferedImage(compWidth, compHeight, BufferedImage.TYPE_INT_ARGB);
                CellRendererPane pane = new CellRendererPane();
                pane.add(component);
                pane.paintComponent(image.createGraphics(), component, pane, component.getBounds());    
                if(icon!=null) {
                        icon.paintIcon(pane, image.getGraphics(), component.getPreferredSize().width, iconY);
                }
                return new ImageIcon(image);
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
                                copyComponentStyle(rendererComponent,checkBox);
                                if (value instanceof TocTreeNodeLayer) {
                                        ILayer layerNode = ((TocTreeNodeLayer) value).getLayer();
                                        Icon layerIcon = TocAbstractRenderer.getLayerIcon(layerNode);
                                        rendererComponent.setIcon(layerIcon);
                                        
                                        String nodeLabel = layerNode.getName();
                                        
                                        if(layerNode.getDataSource()!=null &&
                                                !nodeLabel.equals(layerNode.getDataSource().getName())) {
                                                nodeLabel = I18N.tr("Layer:{0} DataSource :({1})",nodeLabel,layerNode.getDataSource().getName());
                                        }
                                        rendererComponent.setText(nodeLabel);
                                        
                                        
                                        checkBox.setSelected(layerNode.isVisible());
                                } else if(value instanceof TocTreeNodeStyle)  {
                                        Style styleNode = ((TocTreeNodeStyle) value).getStyle();
                                        rendererComponent.setIcon(null);
                                        rendererComponent.setText(styleNode.getName());
                                        checkBox.setSelected(styleNode.isVisible());
                                }
                                panel.add(checkBox,BorderLayout.WEST);
                                panel.add(rendererComponent,BorderLayout.CENTER);
                                panel.doLayout();
                                checkBoxRect= new Rectangle(0, 0, checkBox.getPreferredSize().width, checkBox.getPreferredSize().height);
                                rendererComponent.setIcon(mergeComponentAndIcon(checkBox,rendererComponent.getIcon()));
                        } catch (DriverException ex) {
                                UILOGGER.error(ex);
                        } catch (IOException ex) {
                                UILOGGER.error(ex);
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
