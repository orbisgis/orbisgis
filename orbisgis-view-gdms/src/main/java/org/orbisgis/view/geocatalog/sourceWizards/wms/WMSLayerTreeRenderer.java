/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.sourceWizards.wms;

import com.vividsolutions.wms.MapLayer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import org.orbisgis.view.components.renderers.TreeLaFRenderer;

/**
 * Dedicated renderer for WMS layers in the associated factory.
 * @author Alexis Guéganno
 */
public class WMSLayerTreeRenderer extends TreeLaFRenderer {

    /**
     * Associates this and the given tree.
     * @param tree The JTree that will use this as a renderer.
     */
    public WMSLayerTreeRenderer(JTree tree) {
        super(tree);
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
        Component comp = lookAndFeelRenderer.getTreeCellRendererComponent(tree, value,
                selected, expanded, leaf, row, hasFocus);
        if (comp instanceof JLabel) {
            JLabel lab = (JLabel) comp;
            MapLayer layer = (MapLayer) value;
            lab.setText(layer.getName());
        }
        return comp;
    }

}
