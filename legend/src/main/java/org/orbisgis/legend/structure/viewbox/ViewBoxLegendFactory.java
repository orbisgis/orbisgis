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
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * This factory can be used to quickly create legends representing known viewbox configurations.
 * @author Alexis Guéganno
 */
public final class ViewBoxLegendFactory {

    private ViewBoxLegendFactory(){}

    /**
     * This utility method is used to create {@code ConstantViewBox} instances quickly. It does not test the given
     * {@link org.orbisgis.core.renderer.se.graphic.ViewBox} much, so it's up to the caller to check it can be used
     * to build a {@code ConstantViewbox}.
     * @param vb
     * @return
     * @throws ClassCastException If some argument of the ViewBox are not compatible with a {@code ConstantViewBox}.
     * @throws IllegalArgumentException if both height and width are null in the ViewBox.
     */
    public static ConstantViewBox createConstantViewBox(ViewBox vb) {
        RealLiteral height = (RealLiteral) vb.getHeight();
        RealLiteral width = (RealLiteral) vb.getWidth();
        if(height == null && width == null){
            throw new IllegalArgumentException("you're not supposed to set both height and width of a viewbox to null.");
        } else if(height == null){
            return new ConstantViewBox(new RealLiteralLegend(width),false,vb);
        } else if(width == null){
            return new ConstantViewBox(new RealLiteralLegend(height),true,vb);
        } else {
            return new ConstantViewBox(new RealLiteralLegend(height),new RealLiteralLegend(width),vb);
        }
    }

    /**
     * This utility method is used to create {@code MonovariateProportionalViewBox} instances quickly. It does not test the given
     * {@link org.orbisgis.core.renderer.se.graphic.ViewBox} much, so it's up to the caller to check it can be used
     * to build a {@code MonovariateProportionalViewBox}.
     * @param vb
     * @return
     * @throws ClassCastException If some argument of the ViewBox are not compatible with a {@code MonovariateProportionalViewBox}.
     * @throws IllegalArgumentException if both height and width are null or both are not null in the ViewBox.
     */
    public static MonovariateProportionalViewBox createMonovariateProportionalViewBox(ViewBox vb){
        RealParameter height = vb.getHeight();
        RealParameter width = vb.getWidth();
        if(height != null && width!=null){
            throw new IllegalArgumentException("One of the dimension must be null and the other one an interpolation on" +
                        "the square root of a field");
        }
        if(height == null && width==null){
            throw new IllegalArgumentException("One of the dimension must be null and the other one an interpolation on" +
                        "the square root of a field");
        }
        if (height == null){
            return new MonovariateProportionalViewBox(new SqrtInterpolationLegend((Interpolate2Real)width),false,vb);
        } else {
            return new MonovariateProportionalViewBox(new SqrtInterpolationLegend((Interpolate2Real)height),true,vb);
        }
    }
}
