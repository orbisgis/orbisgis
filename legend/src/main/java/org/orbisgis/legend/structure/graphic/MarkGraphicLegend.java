/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * parameters, taht are stored here are {@code LegendStructure} instances :
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author alexis
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
