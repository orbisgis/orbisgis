/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.common;

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.Locale;

/**
 * This enumeration contains all the units of measure that are allowed in 
 * Symbology Encoding.
 * @author Alexis Guéganno, Maxence Laurent
 */
public enum Uom {

        PX, IN, MM, PT, PERCENT, GM, GFT;

        private static final double PT_IN_INCH = 72.0;
        private static final double MM_IN_INCH = 25.4;
        private static final double IN_IN_FOOT = 12;
        private static final double ONE_THOUSAND = 1000;
        private static final double ONE_HUNDRED = 100;
        private static final I18n I18N = I18nFactory.getI18n(Uom.class, Locale.getDefault(), I18nFactory.FALLBACK);

        /**
         * Gets an array containing the string representation of all the units
         * of mesure contained in this enum.
         * @return
         */
        public static String[] getStrings(){
                Uom[] vals = Uom.values();
                String[] ls = new String[vals.length];
                for(int i=0; i<vals.length; i++){
                        ls[i] = vals[i].name();
                }
                return ls;
        }

        /**
         * Gets an array containing the localized strings associated to the
         * string representation of all the units of mesure contained in this
         * enum.
         * @return
         */
        public static String[] getLocalizedStrings(){
                Uom[] vals = Uom.values();
                String[] ls = new String[vals.length];
                for(int i=0; i<vals.length; i++){
                        ls[i] = vals[i].toLocalizedString();
                }
                return ls;
        }

        /**
         * Create an {@code Uom}instance is {@code s} is equal to the string
         * representation of one instance of this enum, ignoring case. If {@code
         * s} can't be recognized, returned value is {@code PX}.
         * @param s
         * @return
         */
        public static Uom fromString(String s){
                if("IN".equalsIgnoreCase(s)){
                        return IN;
                } else if("MM".equalsIgnoreCase(s)){
                        return MM;
                } else if("PT".equalsIgnoreCase(s)){
                        return PT;
                } else if("PERCENT".equalsIgnoreCase(s)){
                        return PERCENT;
                } else if("GM".equalsIgnoreCase(s)){
                        return GM;
                } else if("GFT".equalsIgnoreCase(s)){
                        return GFT;
                } else {
                        return PX;
                }
        }

        /**
         * Gets the localized representation of this.
         * @return
         */
        public String toLocalizedString(){
                switch (this) {
                        case IN:
                                return I18N.tr("IN");
                        case MM:
                                return I18N.tr("MM");
                        case PT:
                                return I18N.tr("PT");
                        case PERCENT:
                                return I18N.tr("PERCENT");
                        case GM:
                                return I18N.tr("GM");
                        case GFT:
                                return I18N.tr("GFT");
                        default:
                                return I18N.tr("PX");

                }
        }
	/**
	 * Convert a value to the corresponding value in pixel
	 *
	 * Note that converting ground unit to pixel is done by using a constant scale
	 *
	 * @param value the value to convert
	 * @param uom unit of measure for value
	 * @param dpi the current resolution
	 * @param scale the current scale (for converting ground meters and ground feet to media units)
	 * @param v100p the value to return when uom is "percent" and value is 100 (%)
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
				return value * dpi; // [IN] * [PX]/[IN] => [PX]
			case MM:
				return (value / MM_IN_INCH) * dpi; // [MM] * [IN]/[MM] * [PX]/[IN] => [PX]
			case PT: // 1PT == 1/72[IN] whatever dpi is
				return (value / PT_IN_INCH) * dpi; // 1/72[IN] * 72 *[PX]/[IN] => [PX]
			case GM:
				if (scale == null){
					throw new ParameterException("Scale is invalid");
				}
				return (value * ONE_THOUSAND * dpi) / (scale * MM_IN_INCH);
			case GFT:
				if (scale == null){
					throw new ParameterException("Scale is invalid");
				}
				return (value * IN_IN_FOOT * dpi) / (scale);
			case PERCENT:
				if (v100p == null){
					return value;
					//throw new ParameterException("100% value is invalid");
				}
				return value * v100p / ONE_HUNDRED;
			case PX:
			default:
				return value; // [PX]
		}
	}

        /**
         * Build an {@code Uom} from a OGC code that represents a unit of 
         * measure.
         * @param unitOfMeasure
         * @return 
         */
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

        /**
         * Build an OGC code that represents a unit of measure from this
         * {@code Uom}.
         * @return 
         */
	public String toURN() {
		return "urn:ogc:def:uom:se::" + this.name().toLowerCase();
	}
}
