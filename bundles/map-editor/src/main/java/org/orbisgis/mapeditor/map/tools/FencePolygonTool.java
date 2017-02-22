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
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.renderer.se.CompositeSymbolizer;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.Rule;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

public class FencePolygonTool extends AbstractPolygonTool {
        private static Logger UILOGGER = LoggerFactory.getLogger("gui."+FencePolygonTool.class);
        private ILayer layer;
        private static final String FENCE_LAYER_NAME = "fence";
 
        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        protected void polygonDone(Polygon g, MapContext vc, ToolManager tm) throws TransitionException {
                try {
                        if (null != layer) {
                                vc.getLayerModel().remove(layer);
                        }

                        layer = createFenceLayer(g);
                        //Create a style and add it to the fence layer
                        Style fenceStyle = new Style(layer, false);
                        CompositeSymbolizer symbolizer = new CompositeSymbolizer();
                        LineSymbolizer symb = new LineSymbolizer();
                        PenStroke ps = (PenStroke) symb.getStroke();
                        ((SolidFill) (ps).getFill()).setColor(new ColorLiteral(Color.ORANGE));
                        ps.setWidth(new RealLiteral(1));
                        symbolizer.addSymbolizer(symb);
                        Rule r = new Rule();
                        r.setCompositeSymbolizer(symbolizer);
                        fenceStyle.addRule(r);
                        layer.addStyle(fenceStyle);
                        
                        vc.getLayerModel().insertLayer(layer, 0);
                } catch (LayerException e) {
                        UILOGGER.error(i18n.tr("Cannot use fence tool"), e);
                }
            }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return vc.getLayerModel().getLayerCount() > 0;
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return true;
        }

        /**
         * @return Fence layer name with good capitalization
         */
        private String getFenceLayerName(boolean isH2) {
            return TableLocation.parse(FENCE_LAYER_NAME, isH2).toString(isH2);
        }

        private ILayer createFenceLayer(Geometry g) {
                try(Connection connection = mc.getDataManager().getDataSource().getConnection();
                    Statement st = connection.createStatement()) {
                        boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                        String fenceLayer = getFenceLayerName(isH2);
                        if(isH2) {
                            st.execute("CREATE TABLE IF NOT EXISTS " + fenceLayer + " (THE_GEOM POLYGON)");
                        } else {
                            //PostGIS
                            // TODO get SRID of the MapContext to build the fence
                            st.execute("CREATE TABLE IF NOT EXISTS " + fenceLayer + " (THE_GEOM geometry(POLYGON,0))");
                        }
                        st.execute("DELETE FROM " + fenceLayer);
                        st.execute("INSERT INTO " + fenceLayer + " VALUES (ST_PolyFromText('" + g.toString() + "', 0))");
                        return mc.createLayer(fenceLayer);
                } catch (SQLException | LayerException e) {
                        UILOGGER.error(i18n.tr("Cannot create fence layer"), e);
                }
                return null;
        }

        @Override
        public String getName() {
                return i18n.tr("Draw a fence");
        }

    @Override
    public String getTooltip() {
        return i18n.tr("Draw a fence");
    }

    @Override
    public ImageIcon getImageIcon() {
        return MapEditorIcons.getIcon("shape_polygon_edit");
    }
}
