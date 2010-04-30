/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.orbisgis.core.renderer.se.transform;

import java.util.ArrayList;

/**
 * Each implementation represent an affine transformation base on RealParameter.
 * It means each transformation can depends on feature attributes
 *
 * @author maxence
 */
public interface Transformation {
    ArrayList<Matrix> getMatrix();

    public boolean allowedForGeometries();
}
