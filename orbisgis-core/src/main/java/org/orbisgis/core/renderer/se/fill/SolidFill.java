/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.SolidFillType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * A solid fill fills a shape with a solid color (+opacity)
 *
 * @author Maxence Laurent
 */
public final class SolidFill extends Fill {

	private ColorParameter color;
	private RealParameter opacity;

        /**
         * Default value for opacity : {@value DEFAULT_OPACITY}
         */
        public static final double DEFAULT_OPACITY = 1;
        /**
        * Default colour value : {@value GRAY50}
        */
        public static final float GRAY50 = 128.0f;


	/**
	 * Fill with random color and default opacity.
	 */
	public SolidFill() {
		this(new ColorLiteral(), new RealLiteral(DEFAULT_OPACITY));
	}

	/**
	 * Fill with specified color and default opacity
	 * @param c
	 */
	public SolidFill(Color c) {
		this(new ColorLiteral(c), new RealLiteral(DEFAULT_OPACITY));
	}

	/**
	 * Fill with specified color and opacity
	 * @param c
	 * @param opacity
	 */
	public SolidFill(Color c, double opacity) {
		this(new ColorLiteral(c), new RealLiteral(opacity));
	}

	/**
	 * Fill with specified color and opacity
	 * @param c
	 * @param opacity
	 */
	public SolidFill(ColorParameter c, RealParameter opacity) {
		this.setColor(c);
		this.setOpacity(opacity);
	}

        /**
         * Build a {@code SolidFill} using {@code sf}.
         * @param sf
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public SolidFill(JAXBElement<SolidFillType> sf) throws InvalidStyle {
                if (sf.getValue().getColor() != null) {
                        setColor(SeParameterFactory.createColorParameter(sf.getValue().getColor()));
                } else {
                        setColor(new ColorLiteral(new Color((int) GRAY50, (int) GRAY50, (int) GRAY50)));
                }

                if (sf.getValue().getOpacity() != null) {
                            setOpacity(SeParameterFactory.createRealParameter(sf.getValue().getOpacity()));
		} else {
                            setOpacity(new RealLiteral(DEFAULT_OPACITY));
                  }
        }

        /**
         * Set the colour value for this SolidFill.
         * @param color 
         */
	public void setColor(ColorParameter color) {
		this.color = color;
	}

        /**
         * Get the current colour value for this SolidFill.
         */
	public ColorParameter getColor() {
		return color;
	}

        /**
         * Set the opacity value for this SolidFill.
         * @param opacity 
         */
	public void setOpacity(RealParameter opacity) {
                    this.opacity = opacity == null ? new RealLiteral(DEFAULT_OPACITY) : opacity;
                    this.opacity.setContext(RealParameterContext.PERCENTAGE_CONTEXT);
	}

        /**
         * Get the current opacity associated to this SolidFill.
         * @return 
         */
	public RealParameter getOpacity() {
		return opacity;
	}

        /**
        * Return a Java Color according to this SE Solid Fill
        * @param fid
        * @param sds
        * @param selected
        * @param mt
        * @return A java.awt.Color
        * @throws ParameterException
        */
	@Override
	public Paint getPaint(Map<String,Value> map, boolean selected, MapTransform mt) throws ParameterException {

        Color c, ac; // ac stands 4 colour + alpha channel

		if (color != null){
			c = color.getColor(map);
		} else {
                        //We must cast the colours to int values, because we want to use 
                        //GRAY50 to build RGB value - As it equals 128.0f, we need a cast
                        //because Color(float, float, float) needs values between 0 and 1.
		    c = new Color((int) GRAY50, (int) GRAY50, (int) GRAY50);
        }

		if (this.opacity != null) {
		    ac = ColorHelper.getColorWithAlpha(c, this.opacity.getValue(map));
		} else {
            ac = c;
        }

		// Add opacity to the color


		if (selected) {
			ac = ColorHelper.invert(ac);
		}

		return ac;
	}

	@Override
	public void draw(Graphics2D g2, Map<String,Value> map, Shape shp, boolean selected,
            MapTransform mt) throws ParameterException {
		g2.setPaint(getPaint(map, selected, mt));
		g2.fill(shp);
	}

	@Override
	public String toString() {
		return "Color: " + color + " alpha: " + opacity;
	}

	@Override
	public HashSet<String> dependsOnFeature() {
            HashSet<String> ret = null;
            if (color != null) {
                ret = color.dependsOnFeature();
            }
            if (opacity != null) {
                if(ret == null) {
                    ret = opacity.dependsOnFeature();
                } else {
                    ret.addAll(opacity.dependsOnFeature());
                }
            }
            return ret;
	}

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis ua = new UsedAnalysis();
        ua.include(color);
        ua.include(opacity);
        return ua;
    }

	@Override
	public SolidFillType getJAXBType() {
		SolidFillType f = new SolidFillType();

		if (color != null) {
			f.setColor(color.getJAXBParameterValueType());
		}
		if (opacity != null) {
			f.setOpacity(opacity.getJAXBParameterValueType());
		}

		return f;
	}

	@Override
	public JAXBElement<SolidFillType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createSolidFill(this.getJAXBType());
	}
}
