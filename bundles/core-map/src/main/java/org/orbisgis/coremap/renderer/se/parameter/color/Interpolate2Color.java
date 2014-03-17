/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.parameter.color;

import java.awt.Color;
import java.sql.ResultSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.InterpolateType;
import net.opengis.se._2_0.core.InterpolationPointType;
import net.opengis.se._2_0.core.ModeType;


import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.Interpolate;
import org.orbisgis.coremap.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;

/**
 * Interpolate <code>Color</code> values from double values. Interpolation points must be
 * instances of <code>InterpolationPoint&lt;ColorParameter></code>.
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class Interpolate2Color extends Interpolate<ColorParameter, ColorLiteral> implements ColorParameter {

        /**
         * Create a new <code>Interpolate2Color</code> instance, without any 
         * <code>InterpolationPoint&lt;ColorParameter></code> associated with it.
         * They will have to be added before any call to <code>getColor</code>.
         * @param fallback 
         */
        public Interpolate2Color(ColorLiteral fallback) {
                super(fallback);
        }

        /**
         * Create a new <code>Interpolate2Color</code> instance. All its inner 
         * elements are computed from the <code>JAXBElement&lt;InterpolateType></code>
         * given in argument.
         * @param expr
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
         */
        public Interpolate2Color(JAXBElement<InterpolateType> expr) throws InvalidStyle {
                InterpolateType t = expr.getValue();

                this.setFallbackValue(new ColorLiteral(t.getFallbackValue()));

                this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

                if (t.getMode() == ModeType.COSINE) {
                        this.setInterpolationMode(InterpolationMode.COSINE);
                } else if (t.getMode() == ModeType.CUBIC) {
                        this.setInterpolationMode(InterpolationMode.CUBIC);
                } else {
                        System.out.println("Fallback to linear mode !");
                        this.setInterpolationMode(InterpolationMode.LINEAR);
                }

                for (InterpolationPointType ipt : t.getInterpolationPoint()) {
                        InterpolationPoint<ColorParameter> ip = new InterpolationPoint<ColorParameter>();

                        ip.setData(ipt.getData());
                        ip.setValue(SeParameterFactory.createColorParameter(ipt.getValue()));

                        this.addInterpolationPoint(ip);
                }

        }

        /**
         * Retrieve the <code>Color</code> that must be associated to the datum at index
         * <code>fid</code> in <code>sds</code>. The resulting color is obtained by
         * using the value from the <code>DataSet</code>, the 
         * interpolation points and the interpolation method.
         * @param rs
         * @param fid
         * @return
         * The interpolated <code>Color</code>
         */
        @Override
        public Color getColor(ResultSet rs, long fid) throws ParameterException {
                double value = this.getLookupValue().getValue(rs, fid);
                int numPt = getNumInterpolationPoint();
                if (getInterpolationPoint(0).getData() >= value) {
                        return getInterpolationPoint(0).getValue().getColor(rs, fid);
                }
                if (getInterpolationPoint(numPt - 1).getData() <= value) {
                        return getInterpolationPoint(numPt - 1).getValue().getColor(rs, fid);
                }
                int k = getFirstIP(value);
                InterpolationPoint<ColorParameter> ip1 = getInterpolationPoint(k);
                InterpolationPoint<ColorParameter> ip2 = getInterpolationPoint(k + 1);
                double d1 = ip1.getData();
                double d2 = ip2.getData();
                Color c1 = ip1.getValue().getColor(rs, fid);
                Color c2 = ip2.getValue().getColor(rs, fid);
                return computeColor(c1, c2, d1, d2, value);

        }

        /**
         * Retrieve the <code>Color</code> that must be associated to the data
         * stored in {@code map}. The resulting color is obtained by
         * using the value from the <code>DataSet</code>, the
         * interpolation points and the interpolation method.
         * @param map Value map
         * @return
         * The interpolated <code>Color</code>
         */
        @Override
        public Color getColor(Map<String,Object> map) throws ParameterException {
                double value = this.getLookupValue().getValue(map);
                int numPt = getNumInterpolationPoint();
                if (getInterpolationPoint(0).getData() >= value) {
                        return getInterpolationPoint(0).getValue().getColor(map);
                }
                if (getInterpolationPoint(numPt - 1).getData() <= value) {
                        return getInterpolationPoint(numPt - 1).getValue().getColor(map);
                }
                int k = getFirstIP(value);
                InterpolationPoint<ColorParameter> ip1 = getInterpolationPoint(k);
                InterpolationPoint<ColorParameter> ip2 = getInterpolationPoint(k + 1);
                double d1 = ip1.getData();
                double d2 = ip2.getData();
                Color c1 = ip1.getValue().getColor(map);
                Color c2 = ip2.getValue().getColor(map);
                return computeColor(c1, c2, d1, d2, value);
        }

        private Color computeColor(Color c1, Color c2, double d1, double d2, double value){
                switch (this.getMode()) {
                        case CUBIC:
                                return new Color((int) cubicInterpolation(d1, d2, value, c1.getRed(), c2.getRed(), -1.0, -1.0),
                                        (int) cubicInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen(), -1.0, -1.0),
                                        (int) cubicInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue(), -1.0, 1.0));
                        case COSINE:
                                return new Color((int) cosineInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
                                        (int) cosineInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
                                        (int) cosineInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
                        case LINEAR:
                                return new Color((int) linearInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
                                        (int) linearInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
                                        (int) linearInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
                }
                return Color.pink;
        }
}
