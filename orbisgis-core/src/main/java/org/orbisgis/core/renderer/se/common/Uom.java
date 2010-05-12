package org.orbisgis.core.renderer.se.common;

import java.awt.Toolkit;
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
     *
     * @todo return integer !!!
     */
    public static double toPixel(double value, Uom uom, double scale) throws ParameterException {
        if (uom == null){
            return value; // no uom ? => return as Pixel !
        }

        // TODO DPI depends on context ! (e.g pdf 300dpi)
        double dpi = (int)Toolkit.getDefaultToolkit().getScreenResolution();

        switch (uom){
            case IN:
                return value * dpi; // [IN] * [PX]/[IN] => [PX]
            case MM:
                return (value/25.4) * dpi; // [MM] * [IN]/[MM] * [PX]/[IN] => [PX]
            case PT: // 1PT == 1/72[IN] whatever dpi is
                return (value / 72.0) * dpi; // 1/72[IN] * 72 *[PX]/[IN] => [PX]
            case G_M:
                return Uom.toPixel((value/scale)*1000, Uom.MM, scale); //[G_M]/scale*1000 => [MM]
            case G_FT:
                return Uom.toPixel((value/scale)*12, Uom.IN, scale); //[G_M]/scale => [IN]
            // TODO case PERCENT:
            case PX:
            default:
                return value; // [PX]
        }
    }

    public String toURN(){
        return "ogc::se:unit:" + this.name();
    }

    public String toString() {
        return "";
    }

}
