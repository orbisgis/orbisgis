package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSource;

public interface GeometryParameter{
    public Geometry getTheGeom(DataSource ds, int fid);
}