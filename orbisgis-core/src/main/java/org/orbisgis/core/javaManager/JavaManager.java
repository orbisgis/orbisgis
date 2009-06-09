package org.orbisgis.core.javaManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.orbisgis.core.javaManager.parser.ParseException;

public interface JavaManager {

	/**
	 * Compiles and executes the specified java code. The code can be either a
	 * java class with a standard main method to be called or a set of imports
	 * followed by instructions and finished by method declarations. The
	 * compilation errors are reported to the DiagnosticListener. The
	 * OGWorkspace service is used to store intermediate results such as the
	 * compiled class, etc.
	 * 
	 * @param code
	 * @throws InvocationTargetException
	 *             If there is an execution error. The thrown exception will
	 *             contain the cause of the exception
	 * @throws IOException
	 *             Problem accessing the local file system
	 * @throws IllegalArgumentException
	 *             If the specified code is a java class and doesn't contain a
	 *             standard executable main method
	 * @throws CompilationException
	 *             If the code cannot be compiled. This exception does not
	 *             contain the information about the compilation errors. They
	 *             can only be obtained through the listener.
	 * @throws ParseException
	 *             If the code contains syntax errors and cannot be parsed
	 */
	void execute(String code, DiagnosticListener<JavaFileObject> listener)
			throws InvocationTargetException, IOException,
			IllegalArgumentException, CompilationException, ParseException;

	/**
	 * Compiles the specified code and returns a the class resulting from the
	 * compilation process
	 * 
	 * @param code
	 * @return The compiled class. Never null
	 * @throws IOException
	 *             Problem accessing the local file system
	 * @throws CompilationException
	 *             If the code cannot be compiled. This exception does not
	 *             contain the information about the compilation errors. They
	 *             can only be obtained through the listener.
	 * @throws ParseException
	 *             If the code contains syntax errors and cannot be parsed
	 */
	Class<?> compile(String code, DiagnosticListener<JavaFileObject> listener)
			throws IOException, CompilationException, ParseException;

	/**
	 * Adds the specified files to the compile classpath of the manager
	 * 
	 * @param files
	 */
	void addFilesToClassPath(List<File> files);

	/**
	 * Gets an object to explore the packages in the classpath
	 * 
	 * @return
	 */
	PackageReflection getPackageReflection();

}
