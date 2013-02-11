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
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.MonovariateProportionalViewBox;
import org.orbisgis.legend.structure.viewbox.ViewBoxLegendFactory;

/**
 * This class is used to describe instances of {@link MarkGraphic} that embeds a
 * simple proportional symbol configuration, associated to a WKN graphic.
 * @author Alexis Guéganno
 */
public class ProportionalWKNLegend extends ConstantFormWKN {

    /**
     * Builds a new {@code ProportionalWKNLegend} with a default {@link
     * MonovariateProportionalViewBox}.
     */
    public ProportionalWKNLegend(){
        super();
        setViewBoxLegend(new MonovariateProportionalViewBox());
    }

    public  ProportionalWKNLegend(MarkGraphic mark){
        super(mark,
                    new StringLiteralLegend((StringLiteral) mark.getWkn()),
                    ViewBoxLegendFactory.createMonovariateProportionalViewBox(mark.getViewBox()),
                    new ConstantSolidFillLegend((SolidFill)mark.getFill()),
                    new ConstantPenStrokeLegend((PenStroke)mark.getStroke()));
    }
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

    /**
     * Gets the name of the field where the values will be retrieved.
     * @return
     */
    public String getLookupFieldName() {
        return ((MonovariateProportionalViewBox)getViewBoxLegend()).getLookupFieldName();
    }

    /**
     * Sets the name of the field where values will be retrieved.
     * @param name
     */
    public void setLookupFieldName(String name){
        ((MonovariateProportionalViewBox)getViewBoxLegend()).setLookupFieldName(name);
    }

}
