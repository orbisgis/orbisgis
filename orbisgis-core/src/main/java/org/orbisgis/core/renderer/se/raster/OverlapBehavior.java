/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.raster;

/**
 * These values are used to determine how to render objects that overlap. It used for instance
 * while rendering rasters.
 * @author maxence, alexis
 */
public enum OverlapBehavior {
    LATEST_IN_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM

}
