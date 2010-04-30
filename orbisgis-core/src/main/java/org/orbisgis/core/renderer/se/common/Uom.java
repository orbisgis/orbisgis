package org.orbisgis.core.renderer.se.common;

import org.orbisgis.core.renderer.se.parameter.ParameterException;

public enum Uom {
    PX, IN, MM, PT, PERCENT, G_M, G_FT;

    /**
     * Convert a value to the corresponding value in pixel
     *
     * Note that converting ground unit to pixel is done by using a constant scale
     *
     * @param value the value to convert
     * @param uom the value base uom
     * @param dpi the current resolution
     * @param scale the current scale (for converting ground meters and ground feet to media units)
     * @return
     * @throws ParameterException
     */
    public static double toPixel(double value, Uom uom, double dpi, double scale) throws ParameterException {
        switch (uom){
            case PX:
                return value; // [PX]
            case IN:
                return value * dpi; // [IN] * [PX]/[IN] => [PX]
            case MM:
                return (value/25.4) * dpi; // [MM] * [IN]/[MM] * [PX]/[IN] => [PX]
            case PT:
                return (value / 72.0) * dpi; // 1/72[IN] * 72 *[PX]/[IN] => [PX]
            case G_M:
                return Uom.toPixel((value/scale)*1000, Uom.MM, dpi, scale); //[G_M]/scale*1000 => [MM]
            case G_FT:
                return Uom.toPixel((value/scale)*12, Uom.IN, dpi, scale); //[G_M]/scale => [IN]
            default:
                throw new ParameterException("Could not convert " + uom.toString() + " to pixel unit");
        }
    }

}
