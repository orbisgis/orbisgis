/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import java.util.ArrayList;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Scale implements Transformation {

    public Scale(RealParameter x, RealParameter y){
        this.x = x;
        this.y = y;
    }
    
    public Scale(RealParameter xy){
        this.x = xy;
        this.y = xy;
    }


    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
    }

    @Override
    public ArrayList<Matrix> getMatrix(){
        ArrayList<Matrix> array = new ArrayList<Matrix>();
        array.add(new Matrix(x, null, null, y, null, null));
        return array;
    }

    @Override
    public boolean allowedForGeometries(){
        return false;
    }

    private RealParameter x;
    private RealParameter y;
}
