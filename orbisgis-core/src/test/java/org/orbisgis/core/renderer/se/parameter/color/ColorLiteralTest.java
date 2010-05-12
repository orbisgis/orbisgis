/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.color;

import junit.framework.TestCase;

/**
 *
 * @author maxence
 */
public class ColorLiteralTest extends TestCase {
    
    public ColorLiteralTest(String testName) {
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

    
    public void testLiterals(){
        ColorLiteral p1 = new ColorLiteral();
        ColorLiteral p2 = new ColorLiteral();
        ColorLiteral p3 = new ColorLiteral();
        ColorLiteral p4 = new ColorLiteral();
        ColorLiteral p5 = new ColorLiteral();

        System.out.println("Color1: " + p1);
        System.out.println("Color2: " + p2);
        System.out.println("Color3: " + p3);
        System.out.println("Color4: " + p4);
        System.out.println("Color5: " + p5);

        //fail("The test case is a prototype.");
        assert(true);
    }


    @Override
    public void runTest(){
        testLiterals();
    }
}