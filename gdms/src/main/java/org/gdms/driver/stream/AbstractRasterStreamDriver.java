package org.gdms.driver.stream;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.*;
import org.gdms.driver.geotif.AbstractRasterDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;


/**
 *
 * @author doriangoepp
 */


public abstract class AbstractRasterStreamDriver extends AbstractDataSet implements StreamDriver {
    
    protected GeoRaster geoRaster;
    protected RasterMetadata metadata;
    protected Schema schema;
    private DefaultMetadata gdmsMetadata;
    private DataSourceFactory dsf;
    protected Envelope envelope;
    private static final Logger LOG = Logger.getLogger(AbstractRasterDriver.class);
    protected String url;
    
    @Override
    public void open() throws DriverException {
        
    }

    @Override
    public void close() throws DriverException {
        
    }

    @Override
    public void setURL(String url) throws DriverException {
        this.url = new String(url);
        schema = new DefaultSchema(getTypeName() + url); // TODO : donner du sens
        gdmsMetadata = new DefaultMetadata();
        schema.addTable("main", gdmsMetadata);
    }
    
    @Override
    public Schema getSchema() throws DriverException {
        return schema;
    }

    @Override
    public String[] getStreamExtensions() {
        return null;
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
        return SourceManager.WMS;
    }

    /**
     * TODO : que retournent en fait ces deux méthodes getSupportedType et getType ?
     * TODO : Il faudrait sûrement remplacer WMS par STREAM, plus général.
     * @return the data type that the driver uses
     */
    @Override
    public int getType() {
        return SourceManager.WMS | SourceManager.RASTER;
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
