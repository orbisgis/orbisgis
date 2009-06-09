package org.orbisgis.core.javaManager;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.orbisgis.core.Services;
import org.orbisgis.core.javaManager.autocompletion.AbstractVisitor;
import org.orbisgis.core.javaManager.autocompletion.NodeUtils;
import org.orbisgis.core.javaManager.parser.ASTBlockStatement;
import org.orbisgis.core.javaManager.parser.ASTClassOrInterfaceDeclaration;
import org.orbisgis.core.javaManager.parser.ASTCompilationUnit;
import org.orbisgis.core.javaManager.parser.ASTPackageDeclaration;
import org.orbisgis.core.javaManager.parser.ASTScript;
import org.orbisgis.core.javaManager.parser.ASTScriptMethod;
import org.orbisgis.core.javaManager.parser.JavaParser;
import org.orbisgis.core.javaManager.parser.ParseException;
import org.orbisgis.core.javaManager.parser.SimpleNode;
import org.orbisgis.core.javaManager.parser.Token;
import org.orbisgis.core.outputManager.OutputManager;
import org.orbisgis.core.workspace.OGWorkspace;

public class DefaultJavaManager implements JavaManager {

	private JavaCompiler compiler;

	private HashSet<File> additionalBuildPath = new HashSet<File>();

	private HashSet<File> buildPath = null;

	private PackageReflection pr;

	public DefaultJavaManager() {
		compiler = ToolProvider.getSystemJavaCompiler();
	}

	public void execute(String code, DiagnosticListener<JavaFileObject> listener)
			throws InvocationTargetException, IOException,
			IllegalArgumentException, CompilationException, ParseException {
		if (!code.endsWith("\n")) {
			code += "\n";
		}
		Class<?> cl = compile(code, listener);
		try {
			Method m = cl.getMethod("main", String[].class);
			m.invoke(null, (Object) new String[] { "" });
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Cannot access main method", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Cannot find main method", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot execute main method", e);
		}
	}

	@Override
	public Class<?> compile(String code,
			DiagnosticListener<JavaFileObject> userListener)
			throws IOException, CompilationException, ParseException {
		FirstErrorDiagnosticListener listener = new FirstErrorDiagnosticListener(
				userListener);
		StandardJavaFileManager stdFileManager = compiler
				.getStandardFileManager(listener, null, null);
		HashSet<File> systemClassPath = getBuildPath(stdFileManager);
		stdFileManager
				.setLocation(StandardLocation.CLASS_PATH, systemClassPath);

		OGWorkspace ws = (OGWorkspace) Services.getService(OGWorkspace.class);

		CodeInfo codeInfo = prepareCode(code, 0);
		String packageName = codeInfo.getPackageName();
		String className = codeInfo.getClassName();
		code = codeInfo.getModifiedCode();

		File sourceFolder = new File(ws.getTempFolder(), "java-manager"
				+ System.currentTimeMillis());
		File tempDir = new File(sourceFolder, packageName.replaceAll("\\Q.\\E",
				"/"));
		tempDir.mkdirs();
		File tempFile = new File(tempDir, className + ".java");
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(
				tempFile));
		dos.write(code.getBytes());
		dos.close();

		Iterable<? extends JavaFileObject> it = stdFileManager
				.getJavaFileObjectsFromStrings(Arrays.asList(tempFile
						.getAbsolutePath()));
		CompilationTask task = compiler.getTask(new PrintWriter(System.out),
				stdFileManager, listener, null, null, it);
		Boolean compilationResult = task.call();
		if (compilationResult) {
			URLClassLoader urlCL = new URLClassLoader(new URL[] { sourceFolder
					.toURI().toURL() }, DefaultJavaManager.class
					.getClassLoader());
			try {
				return urlCL.loadClass(packageName + "." + className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Bug! Cannot find compiled class", e);
			}
		} else {
			throw new CompilationException("Cannot compile class: "
					+ listener.message);
		}
	}

	private HashSet<File> getBuildPath(StandardJavaFileManager stdFileManager) {
		if (buildPath == null) {
			buildPath = new HashSet<File>();
			Iterator<? extends File> classPath = stdFileManager.getLocation(
					StandardLocation.CLASS_PATH).iterator();
			while (classPath.hasNext()) {
				buildPath.add(classPath.next());
			}
			buildPath.addAll(additionalBuildPath);
		}

		return buildPath;
	}

	/**
	 * Analyzes and possibly modifies specified code to execute it or perform
	 * code completion tasks.
	 *
	 * @param code
	 *            code to analyze
	 * @param caretPosition
	 *            position of the cursor. It is used to calculate the new cursor
	 *            position in case the code is modified
	 * @return
	 * @throws ParseException
	 *             If the code is not a valid java file or script
	 */
	private CodeInfo prepareCode(String code, int caretPosition)
			throws ParseException {
		JavaParser jp = new JavaParser(
				new ByteArrayInputStream(code.getBytes()));
		jp.CompilationUnit();
		ASTCompilationUnit cu = (ASTCompilationUnit) jp.getRootNode();
		if ((cu.jjtGetNumChildren() == 1)
				&& (cu.jjtGetChild(0) instanceof ASTScript)) {
			ScriptClassBuilder scb = new ScriptClassBuilder(code, caretPosition);
			scb.visit((SimpleNode) jp.getRootNode(), null);

			return new CodeInfo(code, scb.getScriptClassCode(), scb
					.getClassName(), scb.getPackage(), scb.getCaretPosition());
		} else if ((cu.jjtGetNumChildren() > 1)
				&& (cu.jjtGetChild(0) instanceof ASTPackageDeclaration)) {
			ClassAndPackageGetter visitor = new ClassAndPackageGetter();
			visitor.visit((SimpleNode) jp.getRootNode(), null);

			return new CodeInfo(code, code, visitor.className, visitor.pack,
					caretPosition);
		} else {
			throw new IllegalArgumentException("No code to execute");
		}
	}

	private class ClassAndPackageGetter extends AbstractVisitor {

		private String className;
		private String pack;

		@Override
		public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
			className = node.first_token.next.image;

			return null;
		}

		@Override
		public Object visit(ASTPackageDeclaration node, Object data) {
			SimpleNode nameNode = (SimpleNode) node.jjtGetChild(0);
			Token t = nameNode.first_token;
			pack = "";
			while (t != nameNode.last_token.next) {
				pack += t.image;
				t = t.next;
			}
			return super.visit(node, data);
		}

	}

	private class ScriptClassBuilder extends AbstractVisitor {

		private int blockStartPos = -1;
		private int methodStartPos;
		private String scriptCode;
		private String classCode;
		private String className;
		private String packageName;
		private int caretPosition;
		private int modifiedCaretPosition;

		public ScriptClassBuilder(String code, int caretPosition) {
			this.scriptCode = code;
			this.caretPosition = caretPosition;
			methodStartPos = code.length();
		}

		public int getCaretPosition() {
			return modifiedCaretPosition;
		}

		@Override
		public Object visit(ASTBlockStatement node, Object data) {
			if (blockStartPos == -1) {
				int line = node.first_token.beginLine;
				int column = node.first_token.beginColumn;
				blockStartPos = NodeUtils.getPosition(scriptCode, line, column);
			}

			return super.visit(node, data);
		}

		@Override
		public Object visit(ASTScriptMethod node, Object data) {
			int line = node.first_token.beginLine;
			int column = node.first_token.beginColumn;
			methodStartPos = NodeUtils.getPosition(scriptCode, line, column);
			return null;
		}

		private void createClass() {
			packageName = "gdms" + System.currentTimeMillis();
			StringBuilder clazz = new StringBuilder("package ").append(
					packageName).append(";\n");
			if (caretPosition < blockStartPos) {
				modifiedCaretPosition = clazz.length() + caretPosition;
			}
			clazz.append(scriptCode.substring(0, blockStartPos));
			clazz.append("import org.orbisgis.outputManager.OutputManager;"
					+ "import org.orbisgis.Services;");
			className = "GDMS" + System.currentTimeMillis();
			clazz.append("public class ").append(className).append("{\n");
			clazz
					.append(
							"public static void main(String[] args) throws Exception {new ")
					.append(className)
					.append(
							"().execute();}public void execute() throws Exception {\n");
			if (caretPosition >= blockStartPos) {
				modifiedCaretPosition = clazz.length() - blockStartPos
						+ caretPosition;
			}
			clazz.append(scriptCode.substring(blockStartPos, methodStartPos));
			clazz.append("}");
			clazz.append(scriptCode.substring(methodStartPos));
			clazz.append("public void print(Object text) {"
					+ getOutputServiceCall() + "om.println(text.toString());}");
			clazz.append(
					"public void help() {" + "OutputManager om = "
							+ "Services.getService(OutputManager.class);"
							+ "om.print(").append(getHelpText()).append(");}");
			clazz.append("}");
			classCode = clazz.toString();
		}

		private String getOutputServiceCall() {
			if (Services.getService(OutputManager.class) == null) {
				// Used in the script generated class
				throw new RuntimeException("OutputManager service is missing");
			}
			return "OutputManager om = (OutputManager) Services"
					+ ".getService(OutputManager.class);";
		}

		private String getHelpText() {
			return "\"Just add the imports and the java code. "
					+ "Functions can be declared at the end of the script. "
					+ "Use templates to acomplish common tasks. \\n\\n"
					+ "Available functions:\\n"
					+ "print(String text): Prints 'text'\\n\\n"
					+ "Example script:\\n"
					+ "import javax.swing.JOptionPane;\\n"
					+ "JOptionPane.showMessageDialog(null, getMessage());\\n"
					+ "private String getMessage() {\\n"
					+ "	return \\\"Hello world\\\";\\n}\\n\"";
		}

		public String getScriptClassCode() {
			if (classCode == null) {
				createClass();
			}

			return classCode;
		}

		public String getPackage() {
			if (packageName == null) {
				createClass();
			}

			return packageName;
		}

		public String getClassName() {
			if (className == null) {
				createClass();
			}

			return className;
		}
	}

	@Override
	public void addFilesToClassPath(List<File> files) {
		additionalBuildPath.addAll(files);
		buildPath = null;
		pr = null;
	}

	@Override
	public PackageReflection getPackageReflection() {
		if (pr == null) {
			try {
				StandardJavaFileManager stdFileManager = compiler
						.getStandardFileManager(null, null, null);
				HashSet<File> systemClassPath = getBuildPath(stdFileManager);
				pr = new PackageReflection(systemClassPath.toArray(new File[0]));
			} catch (LinkageError e) {
				throw new RuntimeException("Bug. Malformed classpaths", e);
			}
		}

		return pr;
	}

	private class FirstErrorDiagnosticListener implements
			DiagnosticListener<JavaFileObject> {

		String message;
		private DiagnosticListener<JavaFileObject> delegate;

		public FirstErrorDiagnosticListener(
				DiagnosticListener<JavaFileObject> delegate) {
			this.delegate = delegate;
		}

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			message = diagnostic.getMessage(Locale.getDefault()) + " at line "
					+ diagnostic.getLineNumber() + " at column "
					+ diagnostic.getColumnNumber();
			if (delegate != null) {
				delegate.report(diagnostic);
			}
		}

	}
}
