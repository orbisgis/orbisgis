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
package org.orbisgis.legend.thematic.constant;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.ConstantColorAndDashesLine;
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
public class UniqueSymbolLine extends ConstantColorAndDashesLine implements IUniqueSymbolLine, StrokeUom {

    private static final I18n I18N = I18nFactory.getI18n(UniqueSymbolLine.class);
    private ConstantPenStroke strokeLegend;

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
     * {@code symbolizer} must really be a unique symbole. Otherwise, you'll face {@code ClassCastException}.
     * @param symbolizer
     * @throws ClassCastException
     * If the {@code Stroke} contaiend in {@code symbolizer} can't be recognized
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
     * Build a new {@code UniqueSymbolLine} instance from the given symbolizer
     * and legend. As the inner analysis is given directly with the symbolizer,
     * we won't check they match. It is up to the caller to check they do.
     * @param symbolizer
     * @param legend
     */
    public UniqueSymbolLine(LineSymbolizer symbolizer, ConstantPenStrokeLegend legend) {
        super(symbolizer);
        strokeLegend = legend;
    }

    /**
     * Gets the {@code LegendStructure} associated to this {@code UniqueSymbolLine}.
     * @return
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
    public void setPenStroke(ConstantPenStroke cpsl){
            strokeLegend = cpsl;
            ((LineSymbolizer)getSymbolizer()).setStroke(strokeLegend.getStroke());
    }

    @Override
    public String getLegendTypeName() {
        return I18N.tr("Unique Symbol - Line");
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolLine";
    }

}
