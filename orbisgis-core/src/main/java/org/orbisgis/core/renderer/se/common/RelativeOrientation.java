package org.orbisgis.core.renderer.se.common;

import net.opengis.se._2_0.core.RelativeOrientationType;

public enum RelativeOrientation {

    PORTRAYAL, NORMAL, NORMAL_UP, LINE;
    // TODO NORMAL_REVERSE, LINE_REVERSE ?

    public static RelativeOrientation readFromToken(RelativeOrientationType rot) {
        String token = rot.value();
        if (token.equalsIgnoreCase("normal")) {
            return RelativeOrientation.NORMAL;
        } else if (token.equalsIgnoreCase("normalup")) {
            return RelativeOrientation.NORMAL_UP;
        } else if (token.equalsIgnoreCase("line")) {
            return RelativeOrientation.LINE;
        } else {
            return RelativeOrientation.PORTRAYAL;
        }
    }

    public RelativeOrientationType getJAXBType() {
        switch (this) {
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
