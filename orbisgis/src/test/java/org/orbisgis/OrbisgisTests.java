package org.orbisgis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;
import org.orbisgis.plugin.view.layerModel.LayerModelTest;
import org.orbisgis.plugin.view.tools.ExtentToolTest;
import org.orbisgis.plugin.view.tools.SelectionTest;

public class OrbisgisTests extends TestCase {
	public static Test suite() {
		PropertyConfigurator.configure(OrbisgisTests.class
				.getResource("log4j.properties"));


		TestSuite suite = new TestSuite(
				"Test for orbisgis");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExtentToolTest.class);
		suite.addTestSuite(SelectionTest.class);
		suite.addTestSuite(LayerModelTest.class);
		//$JUnit-END$
		return suite;
	}

}
