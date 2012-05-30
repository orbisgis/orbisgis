/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.MonovariateProportionalViewBox;

/**
 * This class is used to describe instances of {@link MarkGraphic} that embeds a
 * simple proportional symbol configuration, associated to a WKN graphic.
 * @author alexis
 */
public class ProportionalWKNLegend extends ConstantFormWKN {

    /**
     * Build a new isntance of this {@code Legend} specialization.
     * @param mark
     * @param wknLegend
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ProportionalWKNLegend(MarkGraphic mark, StringLiteralLegend wknLegend,
            MonovariateProportionalViewBox viewBoxLegend, ConstantSolidFillLegend fillLegend, ConstantPenStrokeLegend strokeLegend) {
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Gets the data associated to the first interpolation point.
     * @return
     */
    public double getFirstData() {
        return ((MonovariateProportionalViewBox) getViewBoxLegend()).getFirstData();
    }
    /**
     * Gets the data associated to the second interpolation point.
     * @return
     */
    public double getSecondData() {
        return ((MonovariateProportionalViewBox) getViewBoxLegend()).getSecondData();
    }

    /**
     * Sets the data associated to the first interpolation point.
     * @return
     */
    public void setFirstData(double d) {
        ((MonovariateProportionalViewBox) getViewBoxLegend()).setFirstData(d);
    }

    /**
     * Sets the data associated to the second interpolation point.
     * @return
     */
    public void setSecondData(double d) {
        ((MonovariateProportionalViewBox) getViewBoxLegend()).setSecondData(d);
    }


    /**
     * Get the value of the first interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we retrieve directly the {@code double} it contains.
     * @return
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public double getFirstValue() throws ParameterException {
        return ((MonovariateProportionalViewBox) getViewBoxLegend()).getFirstValue();
    }

    /**
     * Set the value of the first interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we give directly the {@code double} it must contain.
         * @param d
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public void setFirstValue(double d) {
        ((MonovariateProportionalViewBox) getViewBoxLegend()).setFirstValue(d);
    }
    
    /**
     * Get the value of the second interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we retrieve directly the {@code double} it contains.
     * @return
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public double getSecondValue() throws ParameterException {
        return ((MonovariateProportionalViewBox) getViewBoxLegend()).getSecondValue();
    }

    /**
     * Set the value of the second interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we give directly the {@code double} it must contain.
         * @param d
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public void setSecondValue(double d) {
        ((MonovariateProportionalViewBox) getViewBoxLegend()).setSecondValue(d);
    }

}
