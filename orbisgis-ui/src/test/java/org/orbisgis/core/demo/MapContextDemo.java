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
package org.orbisgis.core.demo;

import java.io.File;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.geometryUtils.GeometryConvert;

public class MapContextDemo {

        /**
         * A demo to play with a mapcontext
         */
        static DataSourceFactory dsf = new DataSourceFactory();

        public static void main(String[] args) throws IllegalStateException,
                LayerException, DriverException {

                registerDataManager();
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);

                DefaultMetadata metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.STRING),
                                TypeFactory.createType(Type.GEOMETRY)}, new String[]{
                                "location", "the_geom"});

                MemoryDataSetDriver driver = new MemoryDataSetDriver(metadata);

                for (int i = 0; i < mc.getLayerModel().getLayerCount(); i++) {
                        layer = mc.getLayerModel().getLayer(i);
                        String layerName = layer.getName();

                        Envelope enveloppe = layer.getDataSource().getFullExtent();

                        driver.addValues(new Value[]{ValueFactory.createValue(layerName + ".tiff"),
                                        ValueFactory.createValue(GeometryConvert.toGeometry(enveloppe))});
                }
                dsf.getSourceManager().register("mosaic", driver);
        }

        public static void registerDataManager() {
                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));
        }

        private static DataManager getDataManager() {
                return (DataManager) Services.getService(DataManager.class);
        }
}
