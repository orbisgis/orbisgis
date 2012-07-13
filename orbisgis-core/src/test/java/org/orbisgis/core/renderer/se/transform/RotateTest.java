/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author maxence
 */
public class RotateTest {

    /**
     * Test of getRotation method, of class Rotate. TODO
     */
        @Test
    public void testRotateToAffine() throws ParameterException {
            Rotate r = new Rotate(new RealLiteral(45), new RealLiteral(8), new RealLiteral(7));
            AffineTransform af = r.getAffineTransform(null, Uom.PX, new MapTransform(), 0.0, 0.0);
            assertTrue((af.getType() & AffineTransform.TYPE_GENERAL_ROTATION) != 0);
            assertFalse((af.getType() & AffineTransform.TYPE_FLIP) != 0);
    }
}
