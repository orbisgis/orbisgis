package org.orbisgis.core.renderer.se.common;

import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.RelativeOrientationType;


public enum RelativeOrientation {

	PORTRAYAL, NORMAL, NORMAL_UP, LINE, LINE_UP;
	// TODO NORMAL_REVERSE, LINE_REVERSE ?

	public static RelativeOrientation readFromToken(RelativeOrientationType rot) {
        String token = rot.value();
		if (token.equalsIgnoreCase("normal")) {
			return RelativeOrientation.NORMAL;
		} else if (token.equalsIgnoreCase("normalup")) {
			return RelativeOrientation.NORMAL_UP;
		} else if (token.equalsIgnoreCase("line")) {
			return RelativeOrientation.LINE;
		} else if (token.equalsIgnoreCase("lineUp")) {
			return RelativeOrientation.LINE_UP;
		} else {
			return RelativeOrientation.PORTRAYAL;
		}
	}

    public RelativeOrientationType getJAXBType(){
	   switch (this){
			case LINE:
           case LINE_UP:
			case NORMAL:
			case NORMAL_UP:
			case PORTRAYAL:
		}
		return null;

    }


	public String getAsString() {
		switch (values()[ordinal()]) {
			case LINE:
				return "line";
			case NORMAL:
				return "normal";
			case NORMAL_UP:
				return "normalUp";
			case PORTRAYAL:
				return "portrayal";
		}
		return null;
	}
}
