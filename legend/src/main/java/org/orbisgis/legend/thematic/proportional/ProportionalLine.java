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
package org.orbisgis.legend.thematic.proportional;

import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
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
    implements IInterpolationLegend {

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

    @Override
    public double getFirstData() {
        return strokeLegend.getFirstData();
    }

    @Override
    public double getSecondData() {
        return strokeLegend.getSecondData();
    }

    @Override
    public void setFirstData(double d) {
        strokeLegend.setFirstData(d);
    }

    @Override
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
