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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Coordinate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.h2gis.utilities.GeometryTypeCodes;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.components.sif.AskValidValue;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.TransitionException;

/**
 * Common utility for automatons.
 */
public class ToolUtilities {
    private static final Logger LOGGER = Logger.getLogger(ToolUtilities.class);


    public static double getActiveLayerInitialZ(Connection connection ,MapContext mapContext) {
		String table = mapContext.getActiveLayer().getTableReference();
        if(!table.isEmpty()) {
            TableLocation tableLocation = TableLocation.parse(table);
            try(PreparedStatement st = SFSUtilities.prepareInformationSchemaStatement(connection,
                    tableLocation.getCatalog(),tableLocation.getSchema(),tableLocation.getTable(), "GEOMETRY_COLUMNS","");
                ResultSet rs = st.executeQuery()) {
                if(rs.next()) {
                    switch (rs.getInt("coord_dimension")) {
                        case 3: //XYZ
                        case 4: //XYZM
                            return 0;
                        default: //2 and 5 XYM
                            return Double.NaN;
                    }
                }
            } catch (SQLException ex) {
                LOGGER.debug(ex.getLocalizedMessage(), ex);
                return Double.NaN;
            }
        }
		return Double.NaN;
	}

	/**
	 * Ask the user to input initial values for the non null fields
	 * 
	 * @param sds
	 * @param row
	 * @return
	 * @throws DriverException
	 * @throws TransitionException
	 */
	public static Value[] populateNotNullFields(DataSource sds,
			Value[] row) throws SQLException, TransitionException {
		Value[] ret = new Value[row.length];
		for (int i = 0; i < sds.getFieldCount(); i++) {
			Type type = sds.getFieldType(i);
			if (type.getBooleanConstraint(Constraint.NOT_NULL)
					&& !type.getBooleanConstraint(Constraint.AUTO_INCREMENT) &&
                    row[i]==null) {
				AskValidValue av = new AskValidValue(sds, i);
				if (UIFactory.showDialog(av)) {
					try {
						ret[i] = av.getUserValue();
					} catch (ParseException e) {
						throw new TransitionException("bug!");
					}
				} else {
					throw new TransitionException("Insertion cancelled");
				}
			} else {
				ret[i] = row[i];
			}
		}

		return ret;
	}

	public static List<Coordinate> removeDuplicated(
			List<Coordinate> points) {
		if (points.isEmpty()) {
			return points;
		} else {
			ArrayList<Coordinate> ret = new ArrayList<Coordinate>();
			for (int i = 0; i < points.size() - 1; i++) {
				if (!points.get(i).equals(points.get(i + 1))) {
					ret.add(points.get(i));
				}
			}
			ret.add(points.get(points.size() - 1));
			return ret;
		}
	}

	public static boolean isActiveLayerEditable(MapContext vc) {
		ILayer activeLayer = vc.getActiveLayer();
        return activeLayer != null && activeLayer.getDataSource().isEditable();
	}

	public static boolean isActiveLayerVisible(MapContext vc) {
		ILayer activeLayer = vc.getActiveLayer();
        return activeLayer != null && activeLayer.isVisible();
	}

        /**
         * Check if the selection (features) is greater than a parameter.
         * @param vc
         * @param i
         * @return
         */
        public static boolean isSelectionGreaterOrEqualsThan(MapContext vc, int i){
                ILayer activeLayer = vc.getActiveLayer();
            return activeLayer != null && activeLayer.getSelection().size() >= i;
        }

        /**
         * Check if the selection (features) is equal to a parameter.
         * @param vc
         * @param i
         * @return
         */
        public static boolean isSelectionEqualsTo(MapContext vc, int i){
                ILayer activeLayer = vc.getActiveLayer();
            return activeLayer != null && activeLayer.getSelection().size() == i;
        }

	public static boolean activeSelectionGreaterThan(MapContext vc, int i) {
		ILayer activeLayer = vc.getActiveLayer();
        return activeLayer != null && activeLayer.getSelection().size() >= i;
	}

        /**
         * Check that the geometry of the active layer can contain one of the list of geometry types.
         * of geometry types.
         * @param vc
         * @param geometryTypes
         *      The OGC geometry type codes we are testing. They are listed in {@link org.h2gis.utilities.GeometryTypeCodes}.
         * @return 
         */
	public static boolean geometryTypeIs(Connection connection, MapContext vc, int... geometryTypes) {
		ILayer activeLayer = vc.getActiveLayer();
		if (activeLayer != null && geometryTypes.length > 0) {
			try {
				String table = activeLayer.getTableReference();
                if(!table.isEmpty()) {
                    TableLocation tableLocation = TableLocation.parse(activeLayer.getTableReference());
                    int tableGeoType = SFSUtilities.getGeometryType(connection, tableLocation,
                            SFSUtilities.getGeometryFields(connection,tableLocation).get(0));
                    return tableGeoType == geometryTypes[0] ||  tableGeoType == GeometryTypeCodes.GEOMETRY;
                }
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
        return false;
	}

	public static boolean layerCountGreaterThan(MapContext vc, int i) {
        return vc != null && vc.getLayerModel() != null && vc.getLayerModel().getLayersRecursively().length > i;
    }

	public static boolean isRestrictedPopup(Automaton currentTool) {

		if (currentTool instanceof ZoomInTool) {
			return true;

		} else if (currentTool instanceof ZoomOutTool) {
			return true;
		} else if (currentTool instanceof PanTool) {
			return true;
		}

		return false;

	}
}
