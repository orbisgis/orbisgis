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
public class ExclusionRadius {

    public RealParameter getRadius() {
        return radius;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
    }

    private RealParameter radius;
}
