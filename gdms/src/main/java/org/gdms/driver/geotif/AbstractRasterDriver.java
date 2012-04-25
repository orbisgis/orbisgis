/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver.geotif;

import java.io.File;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.jproj.CoordinateReferenceSystem;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.RasterTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;

public abstract class AbstractRasterDriver extends AbstractDataSet implements FileReadWriteDriver {

        protected GeoRaster geoRaster;
        protected RasterMetadata metadata;
        protected Schema schema;
        private DefaultMetadata gdmsMetadata;
        private DataSourceFactory dsf;
        protected Envelope envelope;
        private static final Logger LOG = Logger.getLogger(AbstractRasterDriver.class);
        private File file;

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening file");
                try {
                        geoRaster = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
                        geoRaster.open();
                        metadata = geoRaster.getMetadata();
                        envelope = metadata.getEnvelope();

                        Constraint[] constraints;
                        Constraint dc = new RasterTypeConstraint(geoRaster.getType());

                        File prj = FileUtils.getFileWithExtension(file, "prj");
                        if (prj != null && prj.exists()) {
                                CoordinateReferenceSystem crs = dsf.getCrsFactory().createFromPrj(prj);
                                if (crs != null) {
                                        CRSConstraint cc = new CRSConstraint(crs);
                                        constraints = new Constraint[]{dc, cc};
                                } else {
                                        constraints = new Constraint[]{dc};
                                }
                        } else {
                                constraints = new Constraint[]{dc};
                        }

                        gdmsMetadata.addField("raster", TypeFactory.createType(Type.RASTER, constraints));


                } catch (IOException e) {
                        throw new DriverException("Cannot access the source: " + file, e);
                }
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        @Override
        public void close() throws DriverException {
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                throw new UnsupportedOperationException("Cannot create an empty raster");
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public void writeFile(File file, DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing file");
                checkMetadata(dataSource.getMetadata());
                if (dataSource.getRowCount() == 0) {
                        throw new DriverException("Cannot store an empty raster");
                } else if (dataSource.getRowCount() > 1) {
                        throw new DriverException("Cannot store more than one raster");
                } else {
                        Value raster = dataSource.getFieldValue(0,
                                MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata()));
                        if (!raster.isNull()) {
                                try {
                                        raster.getAsRaster().save(file.getAbsolutePath());
                                } catch (IOException e) {
                                        throw new DriverException("Cannot write raster", e);
                                }
                        }
                }
        }

        @Override
        public void setFile(File file) {
                this.file = file;
                schema = new DefaultSchema(getTypeName() + file.getAbsolutePath().hashCode());
                gdmsMetadata = new DefaultMetadata();
                schema.addTable("main", gdmsMetadata);
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return null;
        }

        protected void checkMetadata(Metadata metadata) throws DriverException {
                if (metadata.getFieldCount() != 1) {
                        throw new DriverException("This source only "
                                + "accepts an unique raster field");
                } else {
                        Type fieldType = metadata.getFieldType(0);
                        if (fieldType.getTypeCode() != Type.RASTER) {
                                throw new DriverException("Raster field expected");
                        }
                }
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                if (metadata.getFieldCount() != 1) {
                        return "Cannot store more than one raster field";
                } else {
                        int typeCode = metadata.getFieldType(0).getTypeCode();
                        if (typeCode != Type.RASTER) {
                                return "Cannot store " + TypeFactory.getTypeName(typeCode);
                        }
                }
                return null;
        }

        @Override
        public int getSupportedType() {
                return SourceManager.FILE | SourceManager.RASTER;
        }

        @Override
        public int getType() {
                return SourceManager.RASTER | SourceManager.FILE;
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }

        @Override
        public boolean isOpen() {
                // once .open() is called, the content is always accessible
                // thus the driver is always open.
                return true;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                if (fieldId == 0) {
                        return ValueFactory.createValue(geoRaster);
                } else {
                        throw new DriverException("No such field:" + fieldId);

                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return 1;
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                switch (dimension) {
                        case DataSet.X:
                                return new Number[]{envelope.getMinX(), envelope.getMaxX()};
                        case DataSet.Y:
                                return new Number[]{envelope.getMinY(), envelope.getMaxY()};
                        default:
                                return null;
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }
}
