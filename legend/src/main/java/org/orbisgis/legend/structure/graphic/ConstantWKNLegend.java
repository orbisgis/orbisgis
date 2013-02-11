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
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;

/**
 * A Markgraphic, defined with a well-known name, whose all parameters are
 * constant, whatever the input data.
 * @author Alexis Guéganno
 */
public class ConstantWKNLegend extends ConstantFormWKN{

     public ConstantWKNLegend(MarkGraphic mg){
         super(mg,ConstantViewBox.createConstantViewBox(mg.getViewBox()));
     }

    /**
     * Build a new {@code ConstantWKNLegend}, associated to the given {@code
     * MarkGraphic}.
     * @param mark
     * @param wknLegend
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ConstantWKNLegend(MarkGraphic mark, StringLiteralLegend wknLegend,
            ConstantViewBox viewBoxLegend, ConstantSolidFillLegend fillLegend,
            ConstantPenStrokeLegend strokeLegend){
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Get the height of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxHeight() {
        return ((ConstantViewBox)getViewBoxLegend()).getHeight();
    }

    /**
     * Get the width of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxWidth() {
        return ((ConstantViewBox)getViewBoxLegend()).getWidth();
    }

    /**
     * Set the height of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxHeight(Double d) {
        ((ConstantViewBox)getViewBoxLegend()).setHeight(d);
    }

    /**
     * Set the width of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxWidth(Double d) {
        ((ConstantViewBox)getViewBoxLegend()).setWidth(d);
    }
}
