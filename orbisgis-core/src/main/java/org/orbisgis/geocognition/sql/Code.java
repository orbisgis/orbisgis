package org.orbisgis.geocognition.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.orbisgis.Services;
import org.orbisgis.javaManager.CompilationException;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.javaManager.parser.ParseException;

public class Code {

	private String code;
	private ArrayList<Long> errorLines = new ArrayList<Long>();
	private ArrayList<CodeListener> listeners = new ArrayList<CodeListener>();

	public Code(String code) {
		this.code = code;
	}

	public void addCodeListener(CodeListener listener) {
		this.listeners.add(listener);
	}

	public boolean removeCodeListener(CodeListener listener) {
		return this.listeners.remove(listener);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		for (CodeListener listener : listeners) {
			listener.codeChanged(this);
		}
	}

	public Class<?> compile() throws CompilationException {
		JavaManager jm = Services.getService(JavaManager.class);
		Class<?> cl;
		try {
			errorLines.clear();
			cl = jm.compile(code, new DiagnosticListener<JavaFileObject>() {

				@Override
				public void report(
						Diagnostic<? extends JavaFileObject> diagnostic) {
					long lineNumber = diagnostic.getLineNumber();
					errorLines.add(lineNumber);

					System.err.println(diagnostic.getMessage(Locale
							.getDefault()));
					System.err.println(lineNumber);
					System.err.println(diagnostic.getColumnNumber());
				}

			});
		} catch (IOException e) {
			throw new CompilationException("Error compiling the class", e);
		} catch (ParseException e) {
			throw new CompilationException("Cannot parse content", e);
		}

		return cl;
	}

	public ArrayList<Long> getErrorLines() {
		return errorLines;
	}
	
	public int getLineCount() {
		return code.split("\n").length;
	}
}
