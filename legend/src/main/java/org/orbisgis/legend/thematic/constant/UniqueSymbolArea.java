/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.ConstantStrokeArea;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Represents a {@code AreaSymbolizer} whose parameters are constant, whatever
 * the input data are. We expect from its {@code Stroke} :
 * <ul>
 * <li>To be defined with a {@code PenStroke} </li>
 * <li>To have a constant dash array structure</li>
 * <li>To have a constant width</li>
 * <li>To be filled with a constant {@code SolidFill}, and consequently with a
 * constant {@code Color}.</li>
 * </ul>
 * We expect from its {@code Fill} to be a constant {@code SolidFill} instance.
 * @author Alexis Guéganno
 */
public class UniqueSymbolArea extends ConstantStrokeArea implements IUniqueSymbolArea {

    private static final I18n I18N = I18nFactory.getI18n(UniqueSymbolArea.class);
    private ConstantSolidFill fillLegend;
    public static final String NAME = I18N.tr("Unique Symbol - Area");

    /**
     * Builds a new default {@code UniqueSymbolArea} from scratch. It contains a
     * default {@code AreaSymbolizer}, which is consequently constant. The
     * associated {@code LegendStructure} structure is built during initialization.
     */
    public UniqueSymbolArea(){
        super();
        AreaSymbolizer symbolizer = (AreaSymbolizer) getSymbolizer();
        Fill fill = symbolizer.getFill();
        fillLegend = new ConstantSolidFillLegend((SolidFill)fill);
    }

    /**
     * Builds a new {@code UniqueSymbolArea} directly from the given {@code
     * AreaSymbolizer}.
     * @param symbolizer The original symbolizer
     */
    public UniqueSymbolArea(AreaSymbolizer symbolizer){
        super(symbolizer);
        //If we're here, we have a constant stroke : it is either null or an instance
        //of ConstantPenStrokeLegend. Let's analyze the Fill.
        Fill fill = symbolizer.getFill();
        fillLegend = fill == null ? new NullSolidFillLegend() : new ConstantSolidFillLegend((SolidFill)fill);
    }

    /**
     * Builds a new {@code UniqueSymbolArea} from the given {@link AreaParameters}.
     * @param ap The unique symbol configuration.
     */
    public UniqueSymbolArea(AreaParameters ap){
        this();
        getFillLegend().setColor(ap.getFillColor());
        getFillLegend().setOpacity(ap.getFillOpacity());
        getStrokeLegend().setDashArray(ap.getLineDash());
        getStrokeLegend().setLineWidth(ap.getLineWidth());
        getStrokeLegend().setLineColor(ap.getLineColor());
        getStrokeLegend().setLineOpacity(ap.getLineOpacity());
    }

    /**
     * Exposes the inner {@link LegendStructure} that represents the analysis
     * made on the {@link Fill}.
     * @return The fill legend associated to the AreaSymbolizer's fill.
     */
    @Override
    public ConstantSolidFill getFillLegend(){
            return fillLegend;
    }

    @Override
    public ConstantPenStroke getPenStroke() {
        return getStrokeLegend();
    }

    @Override
    public void setPenStroke(ConstantPenStroke cpsl) {
        setStrokeLegend(cpsl);
    }

    /**
     * Sets the legend describing the structure of the fill contained in the
     * inner {@link AreaSymbolizer}. This will update the fill of the {@code
     * AreaSymbolizer} in question.
     * @param cfl The new fill legend that is associated to this unique symbol. Will replace the fill of the inner
     *            symbolizer.
     */
    @Override
    public void setFillLegend(ConstantSolidFill cfl){
            if(cfl == null){
                    fillLegend = new NullSolidFillLegend();
            } else {
                    fillLegend = cfl;
            }
            AreaSymbolizer symbolizer = (AreaSymbolizer) getSymbolizer();
            symbolizer.setFill(fillLegend.getFill());
    }

    @Override
    public String getLegendTypeName() {
        return NAME;
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolArea";
    }

    /**
     * Gets the representation of this symbol as a AreaParameters instance.
     * @return This symbol as an AreaParameters.
     */
    public AreaParameters getAreaParameters(){
        return new AreaParameters(getStrokeLegend().getLineColor(),
                    getStrokeLegend().getLineOpacity(),
                    getStrokeLegend().getLineWidth(),
                    getStrokeLegend().getDashArray(),
                    getFillLegend().getColor(),
                    getFillLegend().getOpacity());
    }
    
}
