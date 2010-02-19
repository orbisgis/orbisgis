/**
 * 
 */
package org.orbisgis.core.javaManager;

public class CodeInfo {
	private String originalCode;
	private String modifiedCode;
	private String className;
	private String packageName;
	private int modifiedCaretPosition;

	public CodeInfo(String originalCode, String modifiedCode, String className,
			String packageName, int modifiedCaretPosition) {
		super();
		this.originalCode = originalCode;
		this.modifiedCode = modifiedCode;
		this.className = className;
		this.packageName = packageName;
		this.modifiedCaretPosition = modifiedCaretPosition;
	}

	public String getOriginalCode() {
		return originalCode;
	}

	public String getModifiedCode() {
		return modifiedCode;
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public int getModifiedCaretPosition() {
		return modifiedCaretPosition;
	}

}