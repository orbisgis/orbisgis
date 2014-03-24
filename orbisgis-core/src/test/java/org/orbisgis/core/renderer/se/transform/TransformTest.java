/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
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
 * @author Maxence Laurent
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
