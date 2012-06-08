/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
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
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
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

                        vc.getLayerModel().insertLayer(layer, 0);
                } catch (LayerException e) {
                        UILOGGER.error(I18N.tr("Cannot use fence tool"), e);
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
                        DataSourceFactory dsf = ((DataManager) Services.getService(DataManager.class)).getDataSourceFactory();



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

                        DataManager dataManager = (DataManager) Services.getService(DataManager.class);
                        return dataManager.createLayer(fenceLayerName);


                } catch (DriverLoadException e) {
                        UILOGGER.error(I18N.tr("Error while recovering fence vectorial layer"), e);
                } catch (DriverException e) {
                        UILOGGER.error(I18N.tr("Cannot create fence layer"), e);

                } catch (LayerException e) {
                        UILOGGER.error(I18N.tr("Cannot create fence layer"), e);

                } catch (IOException e) {
                        UILOGGER.error(I18N.tr("Cannot create fence layer"), e);
                }
                return null;
        }

        @Override
        public String getName() {
                return I18N.tr("Draw a fence");
        }

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("shape_polygon_edit");
        }
}
