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
package org.orbisgis.core;

import java.io.File;

import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexManager;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.layerModel.LayerCollection;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.WMSLayer;

public class DefaultDataManager implements DataManager {

        private static final Logger logger = Logger.getLogger(DefaultDataManager.class);
        private DataSourceFactory dsf;

        public DefaultDataManager(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        @Override
        public DataSourceFactory getDataSourceFactory() {
                return dsf;
        }

        @Override
        public IndexManager getIndexManager() {
                return dsf.getIndexManager();
        }

        @Override
        public SourceManager getSourceManager() {
                return dsf.getSourceManager();
        }

        @Override
        public ILayer createLayer(String sourceName) throws LayerException {
                Source src = ((DataManager) Services.getService(DataManager.class)).getDataSourceFactory().getSourceManager().getSource(sourceName);
                if (src != null) {
                        int type = src.getType();
                        if ((type & (SourceManager.RASTER | SourceManager.VECTORIAL | SourceManager.WMS)) != 0) {
                                try {
                                        DataSource ds = ((DataManager) Services.getService(DataManager.class)).getDataSourceFactory().getDataSource(sourceName);
                                        return createLayer(ds);
                                } catch (DriverLoadException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                } catch (NoSuchTableException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                } catch (DataSourceCreationException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                }
                        } else {
                                throw new LayerException("There is no spatial information: "
                                        + type);
                        }
                } else {
                        throw new LayerException("There is no source "
                                + "registered with the name: " + sourceName);
                }
        }

        @Override
        public ILayer createLayer(DataSource sds) throws LayerException {
                int type = sds.getSource().getType();
                if ((type & SourceManager.WMS) == SourceManager.WMS) {
                        return new WMSLayer(sds.getName(), sds);
                } else {
                        boolean hasSpatialData = true;
                        if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL && 
                                (type & SourceManager.LIVE) != SourceManager.LIVE) {
                                int sfi;
                                try {
                                        sds.open();
                                        sfi = sds.getSpatialFieldIndex();
                                        try {
                                                sds.close();
                                        } catch (AlreadyClosedException e) {
                                                // ignore
                                                logger.debug("Cannot close", e);
                                        }
                                        hasSpatialData = (sfi != -1);
                                } catch (DriverException e) {
                                        throw new LayerException("Cannot check source contents", e);
                                }
                        }
                        if (hasSpatialData) {
                                return new Layer(sds.getName(), sds);
                        } else {
                                throw new LayerException("The source contains no spatial info");
                        }
                }
        }

        @Override
        public ILayer createLayerCollection(String layerName) {
                return new LayerCollection(layerName);
        }

        @Override
        public ILayer createLayer(String name, File file) throws LayerException {
                DataSourceFactory dsf = ((DataManager) Services.getService(DataManager.class)).getDataSourceFactory();
                dsf.getSourceManager().register(name, file);
                try {
                        DataSource dataSource = dsf.getDataSource(name);
                        return createLayer(dataSource);
                } catch (DriverLoadException e) {
                        throw new LayerException("Cannot find a suitable driver for "
                                + file.getAbsolutePath(), e);
                } catch (NoSuchTableException e) {
                        throw new LayerException("bug!", e);
                } catch (DataSourceCreationException e) {
                        throw new LayerException("Cannot instantiate layer", e);
                }
        }

        public ILayer createLayer(File file) throws LayerException {
                DataSourceFactory dsf = ((DataManager) Services.getService(DataManager.class)).getDataSourceFactory();
                String name = dsf.getSourceManager().nameAndRegister(file);
                try {
                        DataSource dataSource = dsf.getDataSource(name);
                        return createLayer(dataSource);
                } catch (DriverLoadException e) {
                        throw new LayerException("Cannot find a suitable driver for "
                                + file.getAbsolutePath(), e);
                } catch (NoSuchTableException e) {
                        throw new LayerException("bug!", e);
                } catch (DataSourceCreationException e) {
                        throw new LayerException("Cannot instantiate layer", e);
                }
        }
}
