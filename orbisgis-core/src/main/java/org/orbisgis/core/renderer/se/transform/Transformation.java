/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Each implementation represent an affine transformation base on RealParameter.
 * It means each transformation can depends on feature attributes
 *
 * @author maxence
 */
public interface Transformation {

    /**
         * This method whall return {@code true} if the transformation can be
         * applied to geometry objects.
         * @return
         * {@code true} if this method can be applied on geometries, false
         * otherwise.
         */
    boolean allowedForGeometries();

    /**
     * Get the AWT {@code AffineTransform} that is represented by this {@code
     * Transformation}.
     * @param sds
     * @param fid
     * @param uom
     * @param mt
     * @param width
     * @param height
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    AffineTransform getAffineTransform(DataSource sds, long fid, Uom uom, MapTransform mt, 
            Double width, Double height) throws ParameterException, IOException;

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBElement} that contains a {@code LabelType} specialization.
     */
    JAXBElement<?> getJAXBElement();

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBType} that represents a {@code LabelType} specialization.
     */
    Object getJAXBType();

    /**
     * Get a String representation of the list of features this {@code Transformation}
     * depends on.
     * @return
     * The features this {@code Transformation} depends on, in a {@code String}.
     */
    HashSet<String> dependsOnFeature();
}
