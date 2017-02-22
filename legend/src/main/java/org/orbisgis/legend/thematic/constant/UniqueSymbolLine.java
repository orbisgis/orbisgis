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
package org.orbisgis.legend.thematic.constant;

import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.ConstantColorAndDashesLine;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Represents a {@code LineSymbolizer} whose parameters are constant, whatever
 * the input data are. We expect from it :
 * <ul>
 * <li>To be defined with a {@code PenStroke} </li>
 * <li>To have a constant dash array structure</li>
 * <li>To have a constant width</li>
 * <li>To be filled with a constant {@code SolidFill}, and consequently with a
 * constant {@code Color}.</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class UniqueSymbolLine extends ConstantColorAndDashesLine implements IUniqueSymbolLine {

    private static final I18n I18N = I18nFactory.getI18n(UniqueSymbolLine.class);
    private ConstantPenStroke strokeLegend;
    public static final String NAME = I18N.tr("Unique Symbol - Line");

    /**
     * Build a new default {@code UniqueSymbolLine} from scratch. It contains a
     * default {@code LineSymbolizer}, which is consequently constant. The
     * associated {@code LegendStructure} structure is built during initialization.
     */
    public UniqueSymbolLine() {
        super(new LineSymbolizer());
        Stroke gr = ((LineSymbolizer)getSymbolizer()).getStroke();
        strokeLegend = new ConstantPenStrokeLegend((PenStroke) gr);
    }

    /**
     * Build a new {@code UniqueSymbolLine} from the given symbolizer. Note that
     * {@code symbolizer} must really be a unique symbol. Otherwise, you'll face {@code ClassCastException}.
     * @param symbolizer The input symbolizer.
     * @throws ClassCastException
     * If the {@code Stroke} contained in {@code symbolizer} can't be recognized
     * as a {@code ConstantPenStrokeLegend}.
     */
    public UniqueSymbolLine(LineSymbolizer symbolizer) {
        super(symbolizer);
        Stroke gr = ((LineSymbolizer)getSymbolizer()).getStroke();
        if(gr instanceof PenStroke){
            strokeLegend = new ConstantPenStrokeLegend((PenStroke) gr);
        }
    }

    /**
     * Builds a new {@code UniqueSymbolLine} using the needed parameters gathered in the given {@code LineParameters}
     * instance.
     * @param lp The {@code LineParameters} instance we'll use to configure our symbol.
     */
    public UniqueSymbolLine(LineParameters lp){
        super(new LineSymbolizer());
        Stroke gr = ((LineSymbolizer)getSymbolizer()).getStroke();
        strokeLegend = new ConstantPenStrokeLegend((PenStroke) gr);
        strokeLegend.setLineColor(lp.getLineColor());
        strokeLegend.setDashArray(lp.getLineDash());
        strokeLegend.setLineOpacity(lp.getLineOpacity());
        strokeLegend.setLineWidth(lp.getLineWidth());
    }

    /**
     * Build a new {@code UniqueSymbolLine} instance from the given symbolizer
     * and legend. As the inner analysis is given directly with the symbolizer,
     * we won't check they match. It is up to the caller to check they do.
     * @param symbolizer  The input symbolizer
     * @param legend The associated legend
     */
    public UniqueSymbolLine(LineSymbolizer symbolizer, ConstantPenStrokeLegend legend) {
        super(symbolizer);
        strokeLegend = legend;
    }

    /**
     * Gets the {@code LegendStructure} associated to this {@code UniqueSymbolLine}.
     * @return The {@code LegendStructure} describing the inner stroke.
     */
    @Override
    public LegendStructure getStrokeLegend() {
        return strokeLegend;
    }

    @Override
    public ConstantPenStroke getPenStroke() {
        return strokeLegend;
    }

    @Override
    public void setPenStroke(ConstantPenStroke penStroke){
            strokeLegend = penStroke;
            ((LineSymbolizer)getSymbolizer()).setStroke(strokeLegend.getStroke());
    }

    @Override
    public String getLegendTypeName() {
        return NAME;
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolLine";
    }

    /**
     * Gets the representation of this {@code UniqueSymbolLine} with the equivalent {@link LineParameters} instance.
     * @return A {@link LineParameters} instance equivalent to this {@code UniqueSymbolLine}.
     */
    public LineParameters getLineParameters(){
        return new LineParameters(getLineColor(), getPenStroke().getLineOpacity(),
                    getPenStroke().getLineWidth(),getPenStroke().getDashArray());
    }

}
