package org.gdms.sql;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.sql.customQuery.CustomQueryTest;
import org.gdms.sql.function.alphanumeric.AlphanumericFunctionTest;
import org.gdms.sql.function.spatial.convert.SpatialFunctionTest;
import org.gdms.sql.function.spatial.geometryProperties.PropertiesFunctionTest;
import org.gdms.sql.function.spatial.io.IOSpatialFunctionTest;
import org.gdms.sql.function.spatial.operators.OperatorsTest;
import org.gdms.sql.function.spatial.predicates.PredicatesTest;
import org.gdms.sql.function.statistics.StatisticFunctionsTest;
import org.gdms.sql.strategies.SQLTest;

public class SQLTestSuite extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.gdms.engine.test");
		suite.addTestSuite(InstructionTest.class);
		suite.addTestSuite(ProcessorTest.class);
		suite.addTestSuite(GrammarTest.class);
		suite.addTestSuite(AlphanumericFunctionTest.class);
		suite.addTestSuite(SpatialFunctionTest.class);
		suite.addTestSuite(PropertiesFunctionTest.class);
		suite.addTestSuite(IOSpatialFunctionTest.class);
		suite.addTestSuite(OperatorsTest.class);
		suite.addTestSuite(OptimizationTests.class);
		suite.addTestSuite(PredicatesTest.class);
		suite.addTestSuite(StatisticFunctionsTest.class);
		suite.addTestSuite(SQLTest.class);
		suite.addTestSuite(SpatialFunctionTest.class);
		suite.addTestSuite(CustomQueryTest.class);
		return suite;
	}
}
