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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsWriter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
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
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

public class FencePolygonTool extends AbstractPolygonTool {
        private static Logger UILOGGER = Logger.getLogger("gui."+FencePolygonTool.class);
        private ILayer layer;
        private String fenceFile = "fence.gdms";
        private final String fenceLayerName = "fence";
 
        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        protected void polygonDone(Polygon g, MapContext vc, ToolManager tm)
                throws TransitionException {
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

        private ILayer createFenceLayer(Geometry g) {
                try {
                        DataSourceFactory dsf = Services.getService(DataManager.class).getDataSourceFactory();



                        File file = new File(dsf.getResultDir() + File.separator + fenceFile);

                        if (file.exists()) {
                                file.delete();
                        }

                        if (dsf.getSourceManager().exists(fenceLayerName)) {
                                dsf.getSourceManager().remove(fenceLayerName);
                        }

                        GdmsWriter writer;
                        writer = new GdmsWriter(file);


                        Metadata md = new DefaultMetadata(new Type[]{TypeFactory.createType(Type.POLYGON)}, new String[]{"the_geom"});

                        writer.writeMetadata(1, md);
                        writer.addValues(new Value[]{ValueFactory.createValue(g)});

                        // write the row indexes
                        writer.writeRowIndexes();
                        // write envelope
                        writer.writeExtent();
                        writer.close();
                        dsf.getSourceManager().register(fenceLayerName, file);

                        DataManager dataManager = Services.getService(DataManager.class);
                        return dataManager.createLayer(fenceLayerName);


                } catch (DriverLoadException e) {
                        UILOGGER.error(i18n.tr("Error while recovering fence vectorial layer"), e);
                } catch (DriverException e) {
                        UILOGGER.error(i18n.tr("Cannot create fence layer"), e);

                } catch (LayerException e) {
                        UILOGGER.error(i18n.tr("Cannot create fence layer"), e);

                } catch (IOException e) {
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
            return OrbisGISIcon.getIcon("shape_polygon_edit");
        }
}
