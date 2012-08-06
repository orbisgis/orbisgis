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
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;

/**
 * This abstract class is a common {@code LegendStructure} description for all the {@code
 * MarkGraphic} instances where the only varying parameters are the dimensions
 * of the associated {@code ViewBox}. That means the {@code Fill}, the {@code
 * Stroke} and the {@code StringParameter} containing the well-known name
 * definition are constants.
 * @author Alexis Guéganno
 */
public abstract class ConstantFormWKN extends MarkGraphicLegend {
    /**
     * Build a default {@code LegendStructure} that describes a {@code MarkGraphic}
     * instance.
     * @param mark
     * @param wknLegend
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ConstantFormWKN(MarkGraphic mark, StringLiteralLegend wknLegend,
            LegendStructure viewBoxLegend, ConstantSolidFill fillLegend,
            ConstantPenStroke strokeLegend) {
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Retrieves the {@code ConstantSolidFill} that is associated to this mark
     * graphic. It can be used to configure safely the underlying {@code
     * MarkGraphic} without threatening the found analysis.
     * @return
     */
    public ConstantSolidFill getSolidFill(){
            return (ConstantSolidFill) getFillLegend();
    }

    /**
     * Retrieves the {@code ConstantPenStroke} that is associated to this mark
     * graphic. It can be used to configure safely the underlying {@code
     * PenStroke} without threatening the found analysis.
     * @return
     */
    public ConstantPenStroke getPenStroke(){
            return (ConstantPenStroke) getStrokeLegend();
    }

    /**
     * Sets the {@code ConstantPenStroke} that is associated to this mark
     * graphic.
     * @param cps
     */
    public void setPenStroke(ConstantPenStroke cps){
            setStrokeLegend(cps);
    }

    /**
     * Gets the well-known name that describes the shape of the inner {@link
     * MarkGraphic}.
     * @return
     */
    public String getWellKnownName(){
            return ((StringLiteralLegend) getWknLegend()).getLiteral().getValue(null);
    }

    /**
     * Sets the well-known name that describes the shape of the inner {@link
     * MarkGraphic}.
     * @param string
     * The new {@code WellKnownName}.
     */
    public void setWellKnownName(String string){
            StringLiteralLegend sll = new StringLiteralLegend(new StringLiteral(string));
            setWknLegend(sll);
    }
}
