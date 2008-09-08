package org.orbisgis.views.geocognition.sync.editor.text.diff;

public class Range {
	private int start, end;

	public Range(int s, int e) {
		start = s;
		end = e;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	@Override
	public String toString() {
		return start + " - " + end;
	}
}
