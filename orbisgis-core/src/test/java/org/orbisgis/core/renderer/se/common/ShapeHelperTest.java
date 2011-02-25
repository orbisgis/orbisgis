/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.common;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author maxence
 */
public class ShapeHelperTest extends TestCase {
    
    public ShapeHelperTest(String testName) {
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


	/**
	 * Test of splitLine method, of class ShapeHelper.
	 */ public void testSplitLine_Shape_double() {
		System.out.println("splitLine");

		Path2D.Double line = new Path2D.Double() {};
		line.moveTo(0,0);
		line.lineTo(10, 10);
		line.lineTo(0, 20);
		line.lineTo(10, 30);
		line.lineTo(0, 40);

		double coords[] = new double[6];
		ArrayList<Shape> result1 = ShapeHelper.splitLine(line, 28.28);


		for (Shape shp : result1){
			PathIterator it = shp.getPathIterator(null);
			System.out.println ("Shape: ");

			while (!it.isDone()){
				it.currentSegment(coords);
				System.out.println ("(" + coords[0] + ";" + " " + coords[1] + ")");
				it.next();
			}
		};

		result1 = ShapeHelper.splitLine(line, 35.0);

		for (Shape shp : result1){
			PathIterator it = shp.getPathIterator(null);
			System.out.println ("Shape: ");

			while (!it.isDone()){
				it.currentSegment(coords);
				System.out.println ("(" + coords[0] + ";" + " " + coords[1] + ")");
				it.next();
			}
		}


		result1 = ShapeHelper.splitLine(line, 70.0);

		for (Shape shp : result1){
			PathIterator it = shp.getPathIterator(null);
			System.out.println ("Shape: ");

			while (!it.isDone()){
				it.currentSegment(coords);
				System.out.println ("(" + coords[0] + ";" + " " + coords[1] + ")");
				it.next();
			}
		}
	}


	 public void testGetPointAt() {
         Path2D.Double path = new Path2D.Double();
         path.moveTo(10, 10);

         path.lineTo(20, 10);
         path.lineTo(20, 20);


         Double pointAt = ShapeHelper.getPointAt(path, 30);

         System.out.println ("PT: " + pointAt.getX() + ";" + pointAt.getY());
         assertEquals(pointAt.getX(), 20.0);
         assertEquals(pointAt.getY(), 30.0);


         path = new Path2D.Double();




     }
}
