package org.orbisgis.view.events;

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
