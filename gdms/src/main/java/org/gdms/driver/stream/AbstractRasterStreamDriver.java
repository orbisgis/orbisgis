/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.driver.stream;

import com.vividsolutions.jts.geom.Envelope;
import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.stream.StreamSource;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.*;
import org.gdms.source.SourceManager;

/**
 *
 * @author doriangoepp
 */
public abstract class AbstractRasterStreamDriver extends AbstractDataSet implements StreamReadWriteDriver {
    
    protected GeoRaster geoRaster;
    protected RasterMetadata metadata;
    protected Schema schema;
    private DefaultMetadata gdmsMetadata;
    private DataSourceFactory dsf;
    protected Envelope envelope;
    protected String url;

    @Override
    public void open(StreamSource streamSource) throws DriverException {
    }
    
    @Override
    public void close() throws DriverException {
    }

    
    @Override
    public Schema getSchema() throws DriverException {
        return schema;
    }

    @Override
    public DataSet getTable(String name) {
        return null;
    }

    @Override
    public void setDataSourceFactory(DataSourceFactory dsf) {
        this.dsf = dsf;
    }

    @Override
    public int getSupportedType() {
        return SourceManager.STREAM;
    }

    /**
     * TODO : que retournent en fait ces deux méthodes getSupportedType et getType ?
     * TODO : Il faudrait sûrement remplacer WMS par STREAM, plus général.
         *
     * @return the data type that the driver uses
     */
    @Override
    public int getType() {
        return SourceManager.STREAM | SourceManager.RASTER;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public TypeDefinition[] getTypesDefinitions() {
        return null;
    }

    @Override
    public String getTypeDescription() {
        return null;
    }

    @Override
    public String getDriverId() {
        return null;
    }

    @Override
    public boolean isCommitable() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public String validateMetadata(Metadata metadata) throws DriverException {
        return null;
    }
}
