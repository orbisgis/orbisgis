package org.gdms.triangulation.sweepLine4CDT;

public class CDTEdge {
	private int begin;
	private int end;

	public CDTEdge(int begin, int end) {
		this.begin = (begin < end) ? begin : end;
		this.end = (begin < end) ? end : begin;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CDTEdge other = (CDTEdge) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		return true;
	}
}