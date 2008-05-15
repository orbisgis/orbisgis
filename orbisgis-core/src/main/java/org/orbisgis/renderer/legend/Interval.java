package org.orbisgis.renderer.legend;

import org.gdms.data.values.Value;

public class Interval {

	private Value start;
	private Value end;
	private boolean minIncluded;
	private boolean maxIncluded;

	public Interval(Value start, boolean minIncluded, Value end,
			boolean maxIncluded) {
		this.start = start;
		this.minIncluded = minIncluded;
		this.end = end;
		this.maxIncluded = maxIncluded;
	}

	public boolean contains(Value value) {
		boolean matchesLower = true;
		if (start != null) {
			if (minIncluded) {
				matchesLower = start.lessEqual(value).getAsBoolean();
			} else {
				matchesLower = start.less(value).getAsBoolean();
			}
		}

		boolean matchesUpper = true;
		if (end != null) {
			if (maxIncluded) {
				matchesUpper = end.greaterEqual(value).getAsBoolean();
			} else {
				matchesUpper = end.greater(value).getAsBoolean();
			}
		}
		return matchesLower && matchesUpper;
	}
}
