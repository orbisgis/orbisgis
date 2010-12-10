package org.orbisgis.core.renderer.se.common;

import org.orbisgis.core.renderer.persistance.se.RelativeOrientationType;

public enum RelativeOrientation {

	PORTRAYAL, NORMAL, NORMAL_UP, LINE, LINE_UP;
	// TODO NORMAL_REVERSE, LINE_REVERSE ?

	public static RelativeOrientation readFromToken(String token) {
		if (token.equalsIgnoreCase("normal")) {
			return RelativeOrientation.NORMAL;
		} else if (token.equalsIgnoreCase("normalUp")) {
			return RelativeOrientation.NORMAL_UP;
		} else if (token.equalsIgnoreCase("line")) {
			return RelativeOrientation.LINE;
		} else if (token.equalsIgnoreCase("lineUp")) {
			return RelativeOrientation.LINE_UP;
		} else {
			return RelativeOrientation.PORTRAYAL;
		}
	}

	public RelativeOrientationType getJaxbType() {
		switch (values()[ordinal()]) {
			case LINE:
				return RelativeOrientationType.LINE;
			case NORMAL:
				return RelativeOrientationType.NORMAL;
			case NORMAL_UP:
				return RelativeOrientationType.NORMAL_UP;
			case PORTRAYAL:
				return RelativeOrientationType.PORTRAYAL;
		}
		return null;
	}
}
