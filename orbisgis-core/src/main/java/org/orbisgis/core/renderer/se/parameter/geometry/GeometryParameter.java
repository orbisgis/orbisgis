package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public interface GeometryParameter{
    public abstract Geometry getTheGeom(Feature feat) throws ParameterException;
}
