/**
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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.mapeditor.map.tools;

import java.util.Observable;
import javax.swing.ImageIcon;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;


/**
 * Tool to select  geometries
 * 
 * @author Fernando Gonzalez Cortes
 */
public class SelectionTool extends AbstractSelectionTool {

	@Override
	public void update(Observable o, Object arg) {

	}

	@Override
	protected ILayer getLayer(MapContext mc) {
        ILayer selectedLayer = null;
        if(mc.getSelectedLayers().length == 1) {
            selectedLayer = mc.getSelectedLayers()[0];
        }
        if(mc.getSelectedStyles().length != 0) {
            selectedLayer = mc.getSelectedStyles()[0].getLayer();
        }
        return selectedLayer;
	}

	@Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {        
            return getAvailableLayers(vc, vc.getBoundingBox()).length>0;
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	public String getName() {
		return i18n.tr("Select a geometry");
	}

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("select");
        }
}
