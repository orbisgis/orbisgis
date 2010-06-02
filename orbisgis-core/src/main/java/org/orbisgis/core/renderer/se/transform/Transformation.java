/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Each implementation represent an affine transformation base on RealParameter.
 * It means each transformation can depends on feature attributes
 *
 * @author maxence
 */
public interface Transformation {
    public boolean allowedForGeometries();

    public AffineTransform getAffineTransform(DataSource ds, long fid, Uom uom) throws ParameterException, IOException;

    public abstract JAXBElement<?> getJAXBElement();

    public abstract Object getJAXBType();
}
