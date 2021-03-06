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

import com.vividsolutions.jts.geom.Coordinate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.editorjdbc.AskValidRow;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.mapeditor.map.tool.Automaton;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.sql.RowSet;

/**
 * Common utility for automatons.
 */
public class ToolUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolUtilities.class);
    private static final I18n I18N = I18nFactory.getI18n(ToolUtilities.class);


    public static double getActiveLayerInitialZ(MapContext mapContext) {
        try(Connection connection = mapContext.getDataManager().getDataSource().getConnection()) {
            return getActiveLayerInitialZ(connection, mapContext);
        } catch (SQLException ex) {
            LOGGER.debug(ex.getLocalizedMessage(), ex);
            return Double.NaN;
        }
    }
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
     * @return False if at least one column does not have default value.
     */
    public static boolean isColumnsHasDefaultValue(Connection connection, String tableReference,String columnName) throws SQLException {
        TableLocation table = TableLocation.parse(tableReference);
        try(ResultSet rs = connection.getMetaData().getColumns(table.getCatalog(), table.getSchema(), table.getTable(), columnName)) {
            if (rs.next()) {
                boolean refuseNull = "NO".equals(rs.getString("IS_NULLABLE"));
                boolean isAutoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
                boolean columnDefault = false;
                try {
                    columnDefault = null != rs.getString("COLUMN_DEF");
                } catch (SQLException ex) {
                    // Ignore
                }
                if (refuseNull && !isAutoIncrement && !columnDefault) {
                    return false;
                }
            }
        }
        return true;
    }

	/**
	 * Ask the user to input initial values for the non null fields
	 */
	public static void populateNotNullFields(DataSource dataSource,String tableReference, RowSet rowSet) throws SQLException, TransitionException {
        // Check if the table does not accept null fields without default values
        ResultSetMetaData meta = rowSet.getMetaData();
        boolean allowNullInsert = true;
        try(Connection connection = dataSource.getConnection()) {
            for (int idColumn = 1; idColumn <= meta.getColumnCount(); idColumn++) {
                if (ResultSetMetaData.columnNullable != meta.isNullable(idColumn) && !isColumnsHasDefaultValue(connection, tableReference, meta.getColumnName(idColumn))) {
                    allowNullInsert = false;
                    break;
                }
            }
        }
        if(!allowNullInsert) {
            // If at least one column require user input then show the add row gui
            AskValidRow rowInput = new AskValidRow(I18N.tr("New feature"), dataSource, tableReference);
            if(UIFactory.showDialog(rowInput)) {
                try {
                    Object[] newRow = rowInput.getRow();
                    for(int idColumn = 0; idColumn < newRow.length; idColumn++) {
                        if(newRow[idColumn] != null) {
                            rowSet.updateObject(idColumn + 1, newRow[idColumn]);
                        }
                    }
                } catch (ParseException ex) {
                    throw new TransitionException(ex);
                }
            }
        }
	}

	public static List<Coordinate> removeDuplicated(
			List<Coordinate> points) {
		if (points.isEmpty()) {
			return points;
		} else {
			ArrayList<Coordinate> ret = new ArrayList<>();
			for (int i = 0; i < points.size() - 1; i++) {
				if (!points.get(i).equals(points.get(i + 1))) {
					ret.add(points.get(i));
				}
			}
			ret.add(points.get(points.size() - 1));
			return ret;
		}
	}

	public static boolean isActiveLayerEditable(MapContext vc) throws SQLException {
        // A primary key must be defined in the table
		ILayer activeLayer = vc.getActiveLayer();
        if(activeLayer == null) {
            return false;
        } else {
            String table = activeLayer.getTableReference();
            if(table!=null && !table.isEmpty()) {
                try(Connection connection = vc.getDataManager().getDataSource().getConnection()) {
                    int pk = JDBCUtilities.getIntegerPrimaryKey(connection, activeLayer.getTableReference());
                    return pk>0;
                }
            } else {
                return false;
            }
        }
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
	public static boolean geometryTypeIs(MapContext vc, int... geometryTypes) {
        Set<Integer> acceptedTypes = new HashSet<>();
        for(int geomType : geometryTypes) {
            acceptedTypes.add(geomType);
        }
		ILayer activeLayer = vc.getActiveLayer();
		if (activeLayer != null && geometryTypes.length > 0) {
			try {
				String table = activeLayer.getTableReference();
                if(!table.isEmpty()) {
                    TableLocation tableLocation = TableLocation.parse(activeLayer.getTableReference());
                    int tableGeoType = SFSUtilities.getGeometryType(vc.getDataManager().getDataSource().getConnection(), tableLocation,"");
                    return acceptedTypes.contains(tableGeoType);
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
