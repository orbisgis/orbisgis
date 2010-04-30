/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.transform;

import org.orbisgis.core.renderer.se.transform.Scale;
import org.orbisgis.core.renderer.se.transform.Rotate;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.transform.Matrix;
import org.orbisgis.core.renderer.se.transform.Translate;
import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author maxence
 */
public class TransformTest extends TestCase {
    
    public TransformTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    public void testRotation(){
        
        System.out.println("Youpii rotation ?");
        Transform t = new Transform();

        Rotate r = new Rotate(new RealLiteral(90.0));

        r.setX(new RealLiteral(10.0));
        r.setY(new RealLiteral(10.0));

        t.addTransformation(r);

        t.consolidateTransformations(false);

        Matrix expected;
        Matrix result;

        expected = new Matrix(0.0, -1.0, 1.0, 0.0, 0.0, 20.0);
        result = t.getConsolidatedMatrix();

        System.out.println("Rotation:");
        System.out.println("Expected:");
        expected.print(null, 0);

        System.out.println("Result:");
        result.print(null, 0);

        assertTrue(expected.equals(result, null, 0));
    }

    
    public void testTranslation(){
        Transform t = new Transform();
        t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));

        t.consolidateTransformations(false);

        Matrix expected = new Matrix(1.0, 0.0, 0.0, 1.0, 10.0, 10.0);
        Matrix result = t.getConsolidatedMatrix();

        System.out.println("Translation:");
        System.out.println("Expected:");
        expected.print(null, 0);

        System.out.println("Result:");
        result.print(null, 0);

        assertTrue(expected.equals(result, null, 0));


        t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));

        t.consolidateTransformations(false);

        expected = new Matrix(1.0, 0.0, 0.0, 1.0, 20.0, 20.0);
        result = t.getConsolidatedMatrix();

        System.out.println("Translation:");
        System.out.println("Expected:");
        expected.print(null, 0);

        System.out.println("Result:");
        result.print(null, 0);

        assertTrue(expected.equals(result, null, 0));

        t.addTransformation(new Translate(new RealLiteral(10.0), new RealLiteral(10.0)));

        t.consolidateTransformations(false);

        expected = new Matrix(1.0, 0.0, 0.0, 1.0, 30.0, 30.0);
        result = t.getConsolidatedMatrix();

        System.out.println("Translation:");
        System.out.println("Expected:");
        expected.print(null, 0);

        System.out.println("Result:");
        result.print(null, 0);

        assertTrue(expected.equals(result, null, 0));
    }

    
    
    public void testScale(){
        Transform t = new Transform();
        t.addTransformation(new Scale(new RealLiteral(10.0)));
        t.consolidateTransformations(false);

        Matrix expected = new Matrix(10.0, 0.0, 0.0, 10.0, 0.0, 0.0);
        Matrix result = t.getConsolidatedMatrix();

        System.out.println("Scale:");
        System.out.println("Expected:");
        expected.print(null, 0);

        System.out.println("Result:");
        result.print(null, 0);

        assertTrue(expected.equals(result, null, 0));
    }

    public void testComposition(){
        Transform t = new Transform();

        t.addTransformation(new Translate(new RealLiteral(5.0), new RealLiteral(0.0)));

        t.addTransformation(new Rotate(new RealLiteral(90.0), // alpha
                              new RealLiteral(10.0), // ox
                              new RealLiteral(0.0))); // oy

        t.addTransformation(new Scale(new RealLiteral(2.0)));

        t.addTransformation(new Translate(new RealLiteral(0.0), new RealLiteral(-10.0)));
        t.addTransformation(new Scale(new RealLiteral(0.5)));
        t.addTransformation(new Rotate(new RealLiteral(90.0), // alpha
                              new RealLiteral(5.0), // ox
                              new RealLiteral(0.0))); // oy

        t.consolidateTransformations(false);

        Matrix result = t.getConsolidatedMatrix();
        Matrix expected = new Matrix(-1.0, 0.0, 0.0, -1.0, 5.0, -5.0);

        System.out.println ("Result: ");
        result.print(null, 90);
        assertTrue(expected.equals(result, null, 0));
    }
}
