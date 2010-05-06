/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * This class contains common element shared by Point,Line,Area an Text Symbolizer.
 * Those vector layers all contains an unit of measure (Uom) and an affine transformation def (transform)
 * @author maxence
 */
public abstract class VectorSymbolizer extends Symbolizer{

    public abstract void draw(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException;


    /**
     * Convert a spatial feature into a LiteShape, should add parameters to handle
     * the scale and to perform a scale dependent generalization !
     * 
     * @param ds the data source
     * @param fid the feature id
     * @return a liteshape representing the spatial feature
     * @throws ParameterException
     * @todo fix maxDist for generalization!
     */
    public LiteShape getLiteShape(DataSource ds, int fid) throws ParameterException, IOException{
        Geometry the_geom = this.getTheGeom(ds, fid); // geom + function

        AffineTransform at = null;

        if (transform != null){
            at = transform.getGraphicalAffineTransform(ds, fid, true);
        }

        double maxDist = 10.0;
        return new LiteShape(the_geom, at, true, maxDist);
    }

    public Transform getTransform() {
        return transform;
    }

    
    @Override
    public Uom getUom(){
        return uom;
    }

    public void setUom(Uom uom){
        if (uom == null)
            this.uom = uom;
        else
            this.uom = Uom.MM;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        transform.setParent(this);
    }


    protected Transform transform;
    protected Uom uom;
    
}
