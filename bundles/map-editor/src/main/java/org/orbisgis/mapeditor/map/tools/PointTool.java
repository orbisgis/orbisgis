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
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import javax.swing.ImageIcon;

import org.h2gis.utilities.GeometryTypeCodes;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

public class PointTool extends AbstractPointTool {

	@Override
	public void update(Observable o, Object arg) {
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
            try {
                return ToolUtilities.geometryTypeIs(vc, GeometryTypeCodes.POINT, GeometryTypeCodes.MULTIPOINT) && ToolUtilities.isActiveLayerEditable(vc);
            } catch (SQLException ex) {
                return false;
            }
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	@Override
	protected void pointDone(Point point, MapContext mc, ToolManager tm)
			throws TransitionException {
		Geometry g = point;
		if (ToolUtilities.geometryTypeIs(mc, GeometryTypeCodes.MULTIPOINT)) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPoint(new Point[] { point });
		}
        ReversibleRowSet rowSet = tm.getActiveLayerRowSet();
        Lock lock = rowSet.getReadLock();
        if(lock.tryLock()) {
            try {
                rowSet.moveToInsertRow();
                rowSet.updateGeometry(g);
                ToolUtilities.populateNotNullFields(mc.getDataManager().getDataSource(), rowSet.getTable(), rowSet);
                rowSet.insertRow();
            } catch (SQLException e) {
                throw new TransitionException(i18n.tr("Cannot Autocomplete the polygon"), e);
            } finally {
                lock.unlock();
            }
        }
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return ToolUtilities.getActiveLayerInitialZ(mapContext);
	}

    @Override
    public String getTooltip() {
        return i18n.tr("Draw a point");
    }

    @Override
	public String getName() {
		return i18n.tr("Draw a point");
	}

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("edition/drawpoint");
        }
}
