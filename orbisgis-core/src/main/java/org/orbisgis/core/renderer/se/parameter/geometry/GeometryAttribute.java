package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import net.opengis.se._2_0.core.GeometryType;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * {@code GeometryAttribute} is a {@link ValueReference} to a geometry in an
 * external file.
 * @author alexis
 */
public class GeometryAttribute extends ValueReference {

    /**
     * Build a new {@code GeometryAttribute} using the JAXB {@code 
     * GeometryType} given in argument.
     * @param geom
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public GeometryAttribute(GeometryType geom) throws InvalidStyle {
        super(geom.getValueReference());
    }


    /**
     * Retrieve the geometry registered in the {@code SpatialDataSourceDecorator}
     * at index {@code fid}.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException 
     */
    public Geometry getTheGeom(DataSource sds, long fid) throws ParameterException {
        try {
            return getFieldValue(sds, fid).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"", ex);
        }
    }


    /**
     * @todo This operation is currently not supported.
     * @return 
     */
    public GeometryType getJAXBGeometryType() {
        GeometryType gt = new GeometryType();
        gt.setValueReference(this.getColumnName());
        //throw new UnsupportedOperationException("Not supported yet.");
        return gt;
    }



}
