package org.orbisgis.core.renderer.se.common;

import org.orbisgis.core.renderer.se.parameter.ParameterException;

public enum Uom {

	PX, IN, MM, PT, PERCENT, GM, GFT;

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
	public static double toPixel(double value, Uom uom, Double dpi, Double scale, Double v100p) throws ParameterException {
		if (uom == null) {
			return value; // no uom ? => return as Pixel !
		}

		// TODO DPI depends on context ! (e.g pdf 300dpi) => Should add dpi into MapTransform

		if (dpi == null && uom != Uom.PX){
			throw new ParameterException("DPI is invalid");
		}

		switch (uom) {
			case IN:
				//System.out.println ("From Inch: " + value + " * " + dpi + " = " + value * dpi);
				return value * dpi; // [IN] * [PX]/[IN] => [PX]
			case MM:
				//System.out.println ("From mm: " + value + "/25.4 * " + dpi + " = " + (value / 25.4) * dpi);
				return (value / 25.4) * dpi; // [MM] * [IN]/[MM] * [PX]/[IN] => [PX]
			case PT: // 1PT == 1/72[IN] whatever dpi is
				return (value / 72.0) * dpi; // 1/72[IN] * 72 *[PX]/[IN] => [PX]
			case GM:
				if (scale == null){
					throw new ParameterException("Scale is invalid");
				}
				//System.out.println ("From gm: "+ ((value * 1000 * dpi) / (scale * 25.4)));
				return (value * 1000 * dpi) / (scale * 25.4);
			case GFT:
				if (scale == null){
					throw new ParameterException("Scale is invalid");
				}
				return (value * 12 * dpi) / (scale);
			case PERCENT:
				if (v100p == null){
					throw new ParameterException("100% value is invalid");
				}
				return value * v100p / 100.0;
			case PX:
			default:
				return value; // [PX]
		}
	}

	public static Uom fromOgcURN(String unitOfMeasure) {
		if (unitOfMeasure.equals("urn:ogc:def:uom:se::in")) {
			return Uom.IN;
		}
		else if (unitOfMeasure.equals("urn:ogc:def:uom:se::px")) {
			return Uom.PX;
		}
		else if (unitOfMeasure.equals("urn:ogc:def:uom:se::pt")) {
			return Uom.PT;
		}
		else if (unitOfMeasure.equals("urn:ogc:def:uom:se::percent")) {
			return Uom.PERCENT;
		}
		else if (unitOfMeasure.equals("urn:ogc:def:uom:se::gm")) {
			return Uom.GM;
		}
		else if (unitOfMeasure.equals("urn:ogc:def:uom:se::gf")) {
			return Uom.GFT;
		} else {
			return Uom.MM;
		}
	}

	public String toURN() {
		return "urn:ogc:def:uom:se::" + this.name().toLowerCase();
	}
}
