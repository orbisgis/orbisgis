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
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.graphic.MarkGraphic;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.FillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.parameter.ParameterLegend;
import org.orbisgis.legend.structure.stroke.StrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.ViewBoxLegend;
import org.orbisgis.legend.structure.viewbox.ViewBoxLegendFactory;

/**
 * <p>
 * The default LegendStructure for a MarkGraphic instance. When proceeding to an anlysis,
 * it will be obtained only if we are unable to recognize a more accurate
 * pattern.</p>
 * <p>This {@code LegendStructure} realization is dependant upon the following
 * parameters, that are stored here are {@code LegendStructure} instances :
 * <ul>
 * <li>The well-known name</li>
 * <li>The view box</li>
 * <li>The fill</li>
 * <li>The stroke</li>
 * </ul>
 * </p>
 * @author Alexis Guéganno
 */
public class MarkGraphicLegend implements LegendStructure {

    private MarkGraphic markGraphic;
    private LegendStructure viewBoxLegend;
    private FillLegend fillLegend;
    private StrokeLegend strokeLegend;
    private ParameterLegend wknLegend;

    /**
     * Builds a new default {@code MarkGraphicLegend} associated to a default
     * {@link MarkGraphic}.
     */
    public MarkGraphicLegend(){
        markGraphic = new MarkGraphic();
        viewBoxLegend = ViewBoxLegendFactory.createConstantViewBox(markGraphic.getViewBox());
        fillLegend = new ConstantSolidFillLegend((SolidFill)markGraphic.getFill());
        strokeLegend = new ConstantPenStrokeLegend((PenStroke)markGraphic.getStroke());
        wknLegend = new StringLiteralLegend((StringLiteral) markGraphic.getWkn());
    }

    /**
     * Build a default {@code LegendStructure} that describes a {@code MarkGraphic}
     * instance.
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public MarkGraphicLegend(MarkGraphic mark, ParameterLegend wknLegend, LegendStructure viewBoxLegend,
            FillLegend fillLegend, StrokeLegend strokeLegend) {
        markGraphic = mark;
        this.wknLegend = wknLegend;
        this.viewBoxLegend = viewBoxLegend;
        this.fillLegend = fillLegend;
        this.strokeLegend = strokeLegend;
    }

    /**
     * Gets the {@code MarkGraphic} associated to this {@code LegendStructure}.
     * @return
     */
    public MarkGraphic getMarkGraphic() {
        return markGraphic;
    }

    /**
     * Sets the {@code MarkGraphic} associated to this {@code LegendStructure}.
     * @param markGraphic
     */
    public void setMarkGraphic(MarkGraphic markGraphic) {
        this.markGraphic = markGraphic;
    }

    /**
     * Get the {@code LegendStructure} associated to the {@code Fill} of the associated
     * {@code MarkGraphic}.
     * @return
     */
    public LegendStructure getFillLegend() {
        return fillLegend;
    }

    /**
     * Set the {@code LegendStructure} associated to the {@code Fill} of the associated
     * @param fillLegend
     */
    public void setFillLegend(FillLegend fillLegend) {
        this.fillLegend = fillLegend;
        markGraphic.setFill(this.fillLegend.getFill());
    }

    /**
     * Get the {@code LegendStructure} associated to the {@code Stroke} contained in the
     * inner {@code MarkGraphic} instance.
     * @return
     */
    public LegendStructure getStrokeLegend() {
        return strokeLegend;
    }

    /**
     * Set the {@code LegendStructure} associated to the {@code Stroke} contained in the
     * inner {@code MarkGraphic} instance.
     * @param strokeLegend
     */
    public void setStrokeLegend(StrokeLegend strokeLegend) {
        this.strokeLegend = strokeLegend;
        markGraphic.setStroke(strokeLegend.getStroke());
    }

    /**
     * Get the {@code LegendStructure} associated to the {@code ViewBox} contained in the
     * inner {@code MarkGraphic} instance.
     * @return
     */
    public LegendStructure getViewBoxLegend() {
        return viewBoxLegend;
    }

    /**
     * Set the {@code LegendStructure} associated to the {@code ViewBox} contained in the
     * inner {@code MarkGraphic} instance.
     * @param viewBoxLegend
     */
    public void setViewBoxLegend(ViewBoxLegend viewBoxLegend) {
        markGraphic.setViewBox(viewBoxLegend.getViewBox());
        this.viewBoxLegend = viewBoxLegend;
    }
    /**
     * Get the legend associated to the inner well-known name representation.
     * @return
     */
    public LegendStructure getWknLegend() {
        return wknLegend;
    }

    /**
     * Sets the legend associated representing the well-known name associated to
     * the inner {@MarkGraphic} of this {@code MarkGraphicLegend}.
     * @param wknLegend
     */
    public void setWknLegend(ParameterLegend wknLegend) {
        this.wknLegend = wknLegend;
        markGraphic.setWkn((StringParameter)wknLegend.getParameter());
    }

}
