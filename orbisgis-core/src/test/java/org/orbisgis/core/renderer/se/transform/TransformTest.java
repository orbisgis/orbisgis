/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import org.junit.Test;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import static org.junit.Assert.*;

/**
 *
 * @author maxence
 */
public class TransformTest {

    public void testRotation() {
        Transform t = new Transform();
        t.setUom(Uom.PX);

        MapTransform mt = new MapTransform();

        Rotate r = new Rotate(new RealLiteral(90.0));

        r.setX(new RealLiteral(10.0));
        r.setY(new RealLiteral(10.0));

        t.addTransformation(r);
        AffineTransform at;
        try {
            at = t.getGraphicalAffineTransform(false, null, mt, null, null);
            AffineTransform rat = AffineTransform.getRotateInstance(90 * Math.PI / 180, 10.0, 10.0);
            assertEquals(at, rat);
        } catch (Exception ex) {
            assertTrue(false);
        }

    }

    @Test
    public void testTranslation() {
        Transform t = new Transform();
        t.setUom(Uom.PX);

        MapTransform mt = new MapTransform();

        t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));

        try {
            AffineTransform at = t.getGraphicalAffineTransform(false, null, mt, null, null);
            AffineTransform rat = AffineTransform.getTranslateInstance(10, 10);
            assertEquals(at, rat);

            t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));
            at = t.getGraphicalAffineTransform(false, null, mt, null, null);
            rat = AffineTransform.getTranslateInstance(20, 20);

            t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));
            at = t.getGraphicalAffineTransform(false, null, mt, null, null);
            rat = AffineTransform.getTranslateInstance(30, 30);
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    @Test
    public void testScale() {
        Transform t = new Transform();
        MapTransform mt = new MapTransform();

        t.setUom(Uom.PX);
        t.addTransformation(new Scale(new RealLiteral(10.0)));
        try {
            AffineTransform at = t.getGraphicalAffineTransform(false, null, mt, null, null);
            AffineTransform rat = AffineTransform.getScaleInstance(10, 10);
            assertEquals(at, rat);
        } catch (Exception e) {
            assertTrue(false);
        }


    }
}
