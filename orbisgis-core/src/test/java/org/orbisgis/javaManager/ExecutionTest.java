package org.orbisgis.javaManager;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.orbisgis.AbstractTest;
import org.orbisgis.Services;
import org.orbisgis.javaManager.autocompletion.Completion;
import org.orbisgis.javaManager.autocompletion.Option;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.outputManager.OutputManager;

public class ExecutionTest extends AbstractTest {

	private static boolean flag = false;

	public static void setFlag(boolean flag) {
		ExecutionTest.flag = flag;
	}

	private TestOutputManager testOutputManager;

	@Override
	protected void setUp() throws Exception {
		testOutputManager = new TestOutputManager();
		Services.registerService(OutputManager.class, "", testOutputManager);
	}

	public void testExecuteMain() throws Exception {
		JavaManager javaManager = (JavaManager) Services
				.getService(JavaManager.class);

		String code = getContent(getClass().getResourceAsStream(
				"TestExecuteMain.jav"));
		testScript(javaManager, code);
	}

	public void testExecuteDifferentNamesAndPackages() throws Exception {
		JavaManager javaManager = (JavaManager) Services
				.getService(JavaManager.class);

		String code = getContent(getClass().getResourceAsStream(
				"TestExecuteMain.jav"));
		testScript(javaManager, code);
		code = getContent(getClass().getResourceAsStream(
				"DifferentNameAndPackage.jav"));
		javaManager.execute(code, new ErrorListener());
		assertTrue(!flag);
	}

	public void testExecuteScript() throws Exception {
		JavaManager javaManager = (JavaManager) Services
				.getService(JavaManager.class);

		String code = getContent(getClass().getResourceAsStream(
				"TestExecuteScript1.jav"));
		testScript(javaManager, code);

		code = getContent(getClass().getResourceAsStream(
				"TestExecuteScript2.jav"));
		testScript(javaManager, code);
	}

	public void testPrint() throws Exception {
		JavaManager javaManager = (JavaManager) Services
				.getService(JavaManager.class);

		String code = getContent(getClass()
				.getResourceAsStream("TestPrint.jav"));
		javaManager.execute(code, new ErrorListener());
		assertTrue(testOutputManager.content.equals("Hello World\n"));
	}

	public void testHelp() throws Exception {
		JavaManager javaManager = (JavaManager) Services
				.getService(JavaManager.class);

		String code = getContent(getClass().getResourceAsStream("TestHelp.jav"));
		javaManager.execute(code, new ErrorListener());
		assertTrue(testOutputManager.content.length() > 0);
	}

	private void testScript(JavaManager javaManager, String code)
			throws InvocationTargetException, IOException,
			CompilationException, ParseException {
		setFlag(false);
		javaManager.execute(code, new ErrorListener());
		assertTrue(flag);
	}

	private String getContent(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		return new String(buffer);
	}

	public void testScriptAutocompletion() throws Exception {
		File file = new File(ExecutionTest.class.getResource(".").toURI());
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				return filename.endsWith(".compl")
						&& filename.startsWith("example");
			}

		});
		Completion completion = new Completion();
		for (File completionFile : files) {
			// File completionFile = new File(
			// "src/test/resources/org/orbisgis/javaManager/example2.compl");
			testFile(completion, true, completionFile);
		}
	}

	public void testClassAutocompletion() throws Exception {
		File file = new File(ExecutionTest.class.getResource(".").toURI());
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				return filename.endsWith(".compl")
						&& filename.startsWith("class");
			}

		});
		Completion completion = new Completion();
		for (File completionFile : files) {
			// File completionFile = new File(
			// "src/test/resources/org/orbisgis/javaManager/class2.compl");
			testFile(completion, false, completionFile);
		}
	}

	private void testFile(Completion completion, boolean isScript,
			File completionFile) throws IOException, FileNotFoundException {
		String content = getContent(new FileInputStream(completionFile));
		int scriptStart = content.indexOf('\n') + 1;
		String header = content.substring(0, scriptStart - 1);
		String[] parts = header.split("\\Q;\\E");
		int caretPosition = Integer.parseInt(parts[0]);
		String script = content.substring(scriptStart);
		StringBuilder sb = new StringBuilder(content);
		sb.insert(scriptStart + caretPosition, "*");
		System.out.println("********** " + completionFile + " **************");
		System.out.println(sb);
		Option[] options = completion.getOptions(script, caretPosition,
				isScript);
		assertTrue(options.length == parts.length - 1);
		HashSet<String> optionSet = new HashSet<String>();
		for (int i = 0; i < options.length; i++) {
			optionSet.add(options[i].getAsString());
		}
		for (int i = 1; i < parts.length; i++) {
			if (optionSet.contains(parts[i])) {
				optionSet.remove(parts[i]);
			} else {
				assertTrue(parts[i]
						+ " is not in the current code completion set\n"
						+ sb.toString(), false);
			}
		}
		if (optionSet.size() > 0) {
			assertTrue(optionSet.iterator().next()
					+ " is an unexpected completion option\n" + sb.toString(),
					false);
		}
	}

	private final class TestOutputManager implements OutputManager {
		private String content = "";

		@Override
		public void makeVisible() {
		}

		@Override
		public void print(String text, Color color) {
			content += text;
		}

		@Override
		public void print(String out) {
			content += out;
		}

		@Override
		public void println(String out) {
			print(out + "\n");
		}

		@Override
		public void println(String text, Color color) {
			print(text + "\n", color);
		}
	}

	private class ErrorListener implements DiagnosticListener<JavaFileObject> {

		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			throw new RuntimeException(diagnostic.getMessage(Locale.ENGLISH));
		}

	}
}