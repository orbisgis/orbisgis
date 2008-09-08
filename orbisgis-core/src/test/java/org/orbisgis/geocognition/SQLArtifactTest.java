package org.orbisgis.geocognition;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.geocognition.sql.CustomQueryJavaCode;
import org.orbisgis.geocognition.sql.FunctionJavaCode;

public class SQLArtifactTest extends AbstractGeocognitionTest {

	private static boolean flag;

	public static void setFlag(boolean f) {
		flag = f;
	}

	private DataManager dm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Register one data source
		dm = Services.getService(DataManager.class);
		dm.getSourceManager().register(
				"mycsv",
				new File("src/test/resources/org/orbisgis/"
						+ "geocognition/test.csv"));
		FunctionManager.remove("A");
		FunctionManager.remove("B");
		QueryManager.remove("A");
		QueryManager.remove("B");
	}

	public void testNewFunctionIsExecutable() throws Exception {
		testExecuteModifyExecute("select * from mycsv where A()",
				"NewJavaFunction", "ModifyFlagFunction", true);
	}

	public void testNewCustomQueryIsExecutable() throws Exception {
		testExecuteModifyExecute("select A();", "NewJavaCustomQuery",
				"ModifyFlagCustomQuery", false);
	}

	private void testExecuteModifyExecute(String sqlInstruction,
			String initialCodeResource, String modifiedCodeResource,
			boolean function) throws ParseException, DriverException,
			ExecutionException, IOException, SemanticException,
			GeocognitionException, Exception {
		// Execute failing
		try {
			dm.getDSF().executeSQL(sqlInstruction);
			assertTrue(false);
		} catch (SemanticException e) {
		}

		// Register code
		String content = getContent(initialCodeResource);
		if (function) {
			gc.addElement("A", new FunctionJavaCode(content));
		} else {
			gc.addElement("A", new CustomQueryJavaCode(content));
		}

		// Execute
		dm.getDSF().executeSQL(sqlInstruction);

		// Modify function
		GeocognitionElement element = gc.getGeocognitionElement("A");
		element.open(null);
		content = getContent(modifiedCodeResource);
		((Code) element.getObject()).setCode(content);
		element.save();

		// Test execution works
		flag = false;
		dm.getDSF().executeSQL(sqlInstruction);
		assertTrue(flag);
		element.close(null);

		// Test execution works after saving geocognition
		saveAndLoad();
		flag = false;
		dm.getDSF().executeSQL(sqlInstruction);
		assertTrue(flag);
	}

	public void testChangeIdChangesFunctionName() throws Exception {
		String content = getContent("NewJavaFunction");
		FunctionJavaCode code = new FunctionJavaCode(content);
		testChangeIdChangesCode(code, "select * from mycsv where A()",
				"select * from mycsv where B()");
	}

	public void testChangeIdChangesCustomQueryName() throws Exception {
		String content = getContent("NewJavaCustomQuery");
		CustomQueryJavaCode code = new CustomQueryJavaCode(content);
		testChangeIdChangesCode(code, "select A();", "select B();");
	}

	private void testChangeIdChangesCode(Code code, String originalSQL,
			String changedIdSQL) throws ParseException, SemanticException,
			DriverException, ExecutionException {
		// Register code
		gc.addElement("A", code);

		// Execute
		dm.getDSF().executeSQL(originalSQL);

		// Change name
		GeocognitionElement element = gc.getGeocognitionElement("A");
		element.setId("B");

		// Execute
		dm.getDSF().executeSQL(changedIdSQL);
		try {
			dm.getDSF().executeSQL(originalSQL);
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	public void testChangeIdNotCompilingChangesFunctionName() throws Exception {
		String content = getContent("NewJavaFunction");
		FunctionJavaCode code = new FunctionJavaCode(content);
		testChangeIdNotCompilingChangesCode(code,
				"select * from mycsv where A()",
				"select * from mycsv where B()");
	}

	public void testChangeIdNotCompilingChangesCustomQueryName()
			throws Exception {
		String content = getContent("NewJavaCustomQuery");
		CustomQueryJavaCode code = new CustomQueryJavaCode(content);
		testChangeIdNotCompilingChangesCode(code, "select A()", "select B()");
	}

	private void testChangeIdNotCompilingChangesCode(Code code,
			String originalSQL, String modifiedIdSQL) throws ParseException,
			SemanticException, DriverException, ExecutionException,
			GeocognitionException {
		// Register code
		gc.addElement("A", code);

		// Execute
		dm.getDSF().executeSQL(originalSQL);

		// Change name not compiling
		String codeContent = code.getCode();
		String removed = codeContent.substring(200);
		codeContent = codeContent.substring(0, 200);
		code.setCode(codeContent);
		GeocognitionElement element = gc.getGeocognitionElement("A");
		element.setId("B");

		// Make it compile
		element.open(null);
		code.setCode(codeContent + removed);
		element.save();
		element.close(null);

		// Execute
		dm.getDSF().executeSQL(modifiedIdSQL);
		try {
			dm.getDSF().executeSQL(originalSQL);
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	public void testChangeFunctionIdConflictingFunctionAndCustomQuery()
			throws Exception {
		String content = getContent("NewJavaFunction");
		FunctionJavaCode code = new FunctionJavaCode(content);
		testChangeIdConflictingFunctionAndCustomQuery(code);
	}

	public void testChangeCustomQueryIdConflictingFunctionAndCustomQuery()
			throws Exception {
		String content = getContent("NewJavaCustomQuery");
		CustomQueryJavaCode code = new CustomQueryJavaCode(content);
		testChangeIdConflictingFunctionAndCustomQuery(code);
	}

	private void testChangeIdConflictingFunctionAndCustomQuery(Code code)
			throws InstantiationException, IllegalAccessException {
		gc.addElement("A", code);

		GeocognitionElement element = gc.getGeocognitionElement("A");
		try {
			String functionName = FunctionManager.getFunctionNames()[0];
			element.setId(FunctionManager.getFunction(functionName).getName());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			String queryName = QueryManager.getQueryNames()[0];
			element.setId(QueryManager.getQuery(queryName).getName());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testRemoveSQLFunction() throws Exception {
		String content = getContent("NewJavaFunction");
		FunctionJavaCode code = new FunctionJavaCode(content);
		String sql = "select * from mycsv where A()";

		testRemoveSQLArtifact(code, sql);
	}

	public void testRemoveCustomQuery() throws Exception {
		String content = getContent("NewJavaCustomQuery");
		CustomQueryJavaCode code = new CustomQueryJavaCode(content);
		String sql = "select A();";

		testRemoveSQLArtifact(code, sql);
	}

	private void testRemoveSQLArtifact(Code code, String sql)
			throws ParseException, SemanticException, DriverException,
			ExecutionException {
		// Register code
		gc.addElement("A", code);

		// Execute
		dm.getDSF().executeSQL(sql);

		// Remove function
		gc.removeElement("A");

		// Should not work
		try {
			dm.getDSF().executeSQL(sql);
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	private String getContent(String resourceName) throws IOException {
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		DataInputStream dis = new DataInputStream(is);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		String content = new String(buffer);
		return content;
	}

}
