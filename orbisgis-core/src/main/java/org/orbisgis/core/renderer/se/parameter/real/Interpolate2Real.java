package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.Interpolate;

public class Interpolate2Real extends Interpolate<RealParameter, RealLiteral> implements RealParameter {


    public Interpolate2Real(RealLiteral fallback){
        super(fallback);
    }

    /**
     * 
     * @param ds
     * @param fid
     * @return
     */
    @Override
    public double getValue(DataSource ds, long fid){
        return 0.0; // TODO compute interpolation
    }


}
