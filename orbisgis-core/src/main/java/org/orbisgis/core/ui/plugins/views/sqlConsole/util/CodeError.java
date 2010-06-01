package org.orbisgis.core.ui.plugins.views.sqlConsole.util;

public class CodeError {

	private int start;

	private int end;
	private String message;

	public CodeError(int startPos, int endPos, String message) {
		this.start = startPos;
		this.end = endPos;
		if (this.end == this.start) {
			this.end = this.start + 1;
		}
		this.message = message;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getMessage() {
		return message;
	}
}
