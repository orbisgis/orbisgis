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
package org.orbisgis.legend.thematic.proportional;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.ConstantColorAndDashesLine;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A {@code ProportionalLine} represents a {@code LineSymbolizer} containing a
 * {@code PenStroke} whose only varying parameter is the width of the line.
 * This width is defined thanks to a linear interpolation made directly on the
 * raw value (i.e. we don't apply any mathematical function to the input values).
 * @author Alexis Guéganno
 */
public class ProportionalLine extends ConstantColorAndDashesLine
    implements LookupFieldName, IInterpolationLegend {

    private ProportionalStrokeLegend strokeLegend;
    private static final I18n I18N = I18nFactory.getI18n(ProportionalLine.class);
    public static final String NAME = I18N.tr("Proportional Line");

    public ProportionalLine(){
            super(new LineSymbolizer());
            LineSymbolizer ls = (LineSymbolizer) getSymbolizer();
            strokeLegend = new ProportionalStrokeLegend();
            ls.setStroke(strokeLegend.getStroke());

    }

    /**
     * Tries to build a new {@code ProportionalLine} from the given {@code
     * LineSymbolizer}. If a {@code ProportionalLine} can't be built, an {@code
     * IllegalArgumentException} is thrown.
     * @param symbolizer
     * @code IllegalArgumentException
     */
    public ProportionalLine(LineSymbolizer symbolizer) {
        super(symbolizer);
        strokeLegend = new ProportionalStrokeLegend((PenStroke) symbolizer.getStroke());
    }

    /**
     * Build a new {@code ProportionalLine} instance from the given symbolizer
     * and legend. As the inner analysis is given directly with the symbolizer,
     * we won't check they match. It is up to the caller to check they do.
     * @param symbolizer
     * @param legend
     */
    public ProportionalLine(LineSymbolizer symbolizer, ProportionalStrokeLegend legend) {
        super(symbolizer);
        strokeLegend = legend;
    }

    @Override
    public String getLookupFieldName(){
            return strokeLegend.getLookupFieldName();
    }

    @Override
    public void setLookupFieldName(String name){
            strokeLegend.setLookupFieldName(name);
    }

    @Override
    public ProportionalStrokeLegend getStrokeLegend() {
        return strokeLegend;
    }

    /**
     * Get the data of the first interpolation point
     * @return
     */
    public double getFirstData() {
        return strokeLegend.getFirstData();
    }

    /**
     * Get the data of the second interpolation point
     * @return
     */
    public double getSecondData() {
        return strokeLegend.getSecondData();
    }

    /**
     * Set the data of the first interpolation point
     * @param d
     */
    public void setFirstData(double d) {
        strokeLegend.setFirstData(d);
    }

    /**
     * Set the data of the second interpolation point
     * @param d
     */
    public void setSecondData(double d) {
        strokeLegend.setSecondData(d);
    }

    @Override
    public double getFirstValue() throws ParameterException {
        return strokeLegend.getFirstValue();
    }

    @Override
    public void setFirstValue(double d) {
        strokeLegend.setFirstValue(d);
    }

    @Override
    public double getSecondValue() throws ParameterException {
        return strokeLegend.getSecondValue();
    }

    @Override
    public void setSecondValue(double d) {
        strokeLegend.setSecondValue(d);
    }

    @Override
    public String getLegendTypeName() {
        return NAME;
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.proportional.ProportionalLine";
    }

}
