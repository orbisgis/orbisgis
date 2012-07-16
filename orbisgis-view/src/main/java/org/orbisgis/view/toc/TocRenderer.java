/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.view.toc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Style;

public class TocRenderer extends TocAbstractRenderer {
        private static Logger UILOGGER = Logger.getLogger("gui."+ TocRenderer.class);

        private TreeCellRenderer lookAndFeelRenderer;
        private JTree tree;
        private JCheckBox lastCheckBox;
        /**
         * Install this renderer inside the tree
         * @param tree 
         */
        public static void install( JTree tree) {
                TocRenderer tocRenderer = new TocRenderer(tree);
                tocRenderer.initialize();
                tree.setCellRenderer(tocRenderer);
        }
        
        /**
         * Update the native renderer
         * @warning Using only by PropertyChangeListener on UI property
         */
        public void updateLFRenderer() {
                lookAndFeelRenderer = new JTree().getCellRenderer();
        }
        
        /**
         * Listen for the arrival of Look&Feel
         */
        private void initialize() {
                updateLFRenderer();
                tree.addPropertyChangeListener("UI",
                        EventHandler.create(PropertyChangeListener.class,this,"updateLFRenderer"));
        }
        /**
         * Private constructor, use the static install method
         * @param lfRenderer 
         */
        private TocRenderer(JTree tree) {
                this.tree = tree;
        }
        
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
                Component nativeRendererComp = lookAndFeelRenderer.getTreeCellRendererComponent(
                        tree, value, selected, expanded, leaf, row, hasFocus); 
                if(nativeRendererComp instanceof DefaultTreeCellRenderer) {
                        DefaultTreeCellRenderer rendererComponent = (DefaultTreeCellRenderer) nativeRendererComp;
                        try {
                                JPanel panel = new JPanel(new BorderLayout());
                                panel.setOpaque(false);
                                JCheckBox checkBox = new JCheckBox();
                                checkBox.setBackground(rendererComponent.getBackground());
                                checkBox.setOpaque(rendererComponent.isOpaque());
                                checkBox.setBorder(rendererComponent.getBorder());
                                if (value instanceof ILayer) {
                                        ILayer layerNode = (ILayer) value;
                                        Icon layerIcon = TocAbstractRenderer.getLayerIcon(layerNode);
                                        rendererComponent.setIcon(layerIcon);
                                        rendererComponent.setText(layerNode.getName());
                                        checkBox.setSelected(layerNode.isVisible());
                                } else if(value instanceof Style)  {
                                        Style styleNode = (Style) value;
                                        rendererComponent.setIcon(null);
                                        rendererComponent.setText(styleNode.getName());
                                        checkBox.setSelected(styleNode.isVisible());
                                }
                                panel.add(checkBox,BorderLayout.WEST);
                                panel.add(rendererComponent,BorderLayout.CENTER);
                                lastCheckBox = checkBox;
                                return panel;
                        } catch (DriverException ex) {
                                UILOGGER.error(ex);
                        } catch (IOException ex) {
                                UILOGGER.error(ex);
                        }
                }
                return nativeRendererComp;
	}

	public Rectangle getCheckBoxBounds() {
                if(lastCheckBox!=null) {
                        return lastCheckBox.getBounds();
                } else {
                        return new Rectangle(0,0);
                }
	}
}
