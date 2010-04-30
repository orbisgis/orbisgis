/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import java.util.ArrayList;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Translate implements Transformation {

    public Translate(RealParameter x, RealParameter y){
        this.x = x;
        this.y = y;
    }


    @Override
    public ArrayList<Matrix> getMatrix(){
        ArrayList<Matrix> array = new ArrayList<Matrix>();
        array.add(new Matrix(new RealLiteral(1.0), null, null, new RealLiteral(1.0), x, y));
        return array;
    }

        
    @Override
    public boolean allowedForGeometries(){
        return true;
    }

    private RealParameter x;
    private RealParameter y;
}
