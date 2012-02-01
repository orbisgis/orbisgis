/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.base.events;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class EventNameTest 
    extends TestCase
{
    private final static EventName root=new EventName("feet");
    private final static EventName leaf=new EventName(root,"body");
    private final static EventName subleaf=new EventName(leaf,"head");
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EventNameTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EventNameTest.class );
    }

    /**
     * Test the function getLeftEvents of EventName class
     */
    public void test_getLeftEvents()
    {
        EventName evt = subleaf;
        EventName[] bodyAndFeet = evt.getLeftEvents();
        assertTrue(  bodyAndFeet[0].toString().equals(leaf.toString()));
        assertTrue(  bodyAndFeet[1].toString().equals(root.toString()));
        (new EventName()).getLeftEvents();
        root.getLeftEvents();
    }
}
