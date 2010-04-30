/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import java.util.ArrayList;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperatorType;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperatorType;

/**
 *
 * @author maxence
 */
public class Rotate implements Transformation {

    public Rotate(RealParameter rotation){
        this.rotation = rotation;
        this.x = null;
        this.y = null;
    }

    public Rotate(RealParameter rotation, RealParameter ox, RealParameter oy){
        this.rotation = rotation;
        this.x = ox;
        this.y = oy;
    }

    public RealParameter getRotation() {
        return rotation;
    }

    public void setRotation(RealParameter rotation) {
        this.rotation = rotation;
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
    public boolean allowedForGeometries(){
        return false;
    }

    @Override
    public ArrayList<Matrix> getMatrix(){
        ArrayList<Matrix> array = new ArrayList<Matrix>();
        if (x != null && y != null){

            RealParameter cos = new RealUnitaryOperator(rotation, RealUnitaryOperatorType.COS);
            RealParameter sin = new RealUnitaryOperator(rotation, RealUnitaryOperatorType.SIN);
            RealParameter minSin = new RealBinaryOperator(RealLiteral.ZERO, sin, RealBinaryOperatorType.SUB);


            array.add(new Matrix(new RealLiteral(1.0), null,  // A B
                                 null, new RealLiteral(1.0),  // C D
                                 new RealBinaryOperator(RealLiteral.ZERO,
                                                        x,
                                                        RealBinaryOperatorType.SUB), // E
                                 new RealBinaryOperator(RealLiteral.ZERO,
                                                        y,
                                                        RealBinaryOperatorType.SUB))); // F
            
            array.add(new Matrix(cos, sin, minSin, cos, null, null));

            array.add(new Matrix(new RealLiteral(1.0), null, null, new RealLiteral(1.0), x, y));
            
        }
        else{ // Only rotation (no center specified...)
            RealParameter cos = new RealUnitaryOperator(rotation, RealUnitaryOperatorType.COS);
            RealParameter sin = new RealUnitaryOperator(rotation, RealUnitaryOperatorType.SIN);
            RealParameter minSin = new RealBinaryOperator(RealLiteral.ZERO, sin, RealBinaryOperatorType.SUB);

            array.add(new Matrix(cos, sin, minSin, cos, null, null));
        }

        return array;
    }

    private RealParameter x;
    private RealParameter y;
    private RealParameter rotation;

}
