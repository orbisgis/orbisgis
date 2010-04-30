/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class ExclusionRectangle {

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

    

    private RealParameter x;
    private RealParameter y;
}
