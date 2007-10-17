package org.orbisgis;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;
import org.orbisgis.plugin.view.layerModel.LayerModelTest;
import org.orbisgis.plugin.view.tools.ExtentToolTest;
import org.orbisgis.plugin.view.tools.SelectionTest;

public class OrbisgisTests extends TestCase {
	public static Test suite() {
		URL resource = OrbisgisTests.class
				.getResource("/org/orbisgis/log4j.properties");
		if (resource == null) {
			throw new RuntimeException(resource.toExternalForm());
		}
		System.err.println("\t\t\t"+resource.toString());
		PropertyConfigurator.configure(resource);


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
