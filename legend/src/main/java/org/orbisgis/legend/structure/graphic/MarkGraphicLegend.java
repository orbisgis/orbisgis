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
import org.orbisgis.legend.LegendStructure;

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
    private LegendStructure fillLegend;
    private LegendStructure strokeLegend;
    private LegendStructure wknLegend;

    /**
     * Build a default {@code LegendStructure} that describes a {@code MarkGraphic}
     * instance.
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public MarkGraphicLegend(MarkGraphic mark, LegendStructure wknLegend, LegendStructure viewBoxLegend,
            LegendStructure fillLegend, LegendStructure strokeLegend) {
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
    public void setFillLegend(LegendStructure fillLegend) {
        this.fillLegend = fillLegend;
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
    public void setStrokeLegend(LegendStructure strokeLegend) {
        this.strokeLegend = strokeLegend;
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
    public void setViewBoxLegend(LegendStructure viewBoxLegend) {
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
    public void setWknLegend(LegendStructure wknLegend) {
        this.wknLegend = wknLegend;
    }

}
