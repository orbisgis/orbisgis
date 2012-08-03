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

import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.MarkGraphicAnalyzer;
import org.orbisgis.legend.structure.graphic.ConstantFormWKN;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;
import org.orbisgis.legend.thematic.ConstantFormPoint;

/**
 * A {@code ProportionalPoint} is a {@link ConstantFormPoint} whose {@code
 * ViewBox} is defined so that it can be recognized as a {@code
 * MonovariateProportionalViewBox}.
 * @author Alexis Guéganno
 */
public class ProportionalPoint extends ConstantFormPoint  {

    private ProportionalWKNLegend markGraphic;

    /**
     * Builds a new {@code ProportionalPoint}. It has default {@code MarkGraphic}
     * parameters except for the {@code ViewBox}, of course.
     */
    public ProportionalPoint(){
        super();
        markGraphic = new ProportionalWKNLegend();
        PointSymbolizer ps = (PointSymbolizer) getSymbolizer();
        ps.getGraphicCollection().delGraphic(0);
        ps.getGraphicCollection().addGraphic(markGraphic.getMarkGraphic());
    }

    /**
     * Tries to build an instance of {@code ProportionalPoint} using the given
     * {@code PointSymbolizer}.
     * @param symbolizer
     * @throws IllegalArgumentException
     * If {@code pointSymbolizer} can't be recognized as a valid {@code
     * UniqueSymbolPoint}.
     */
    public ProportionalPoint(PointSymbolizer pointSymbolizer) {
        super(pointSymbolizer);
        if(pointSymbolizer.getGraphicCollection().getNumGraphics() == 1){
            Graphic gr = pointSymbolizer.getGraphicCollection().getGraphic(0);
            if(gr instanceof MarkGraphic){
                LegendStructure mgl = new MarkGraphicAnalyzer((MarkGraphic) gr).getLegend();
                if(mgl instanceof ProportionalWKNLegend){
                    markGraphic = (ProportionalWKNLegend) mgl;
                }  else {
                    throw new IllegalArgumentException("We can't analyze yet symbolizers "
                            + "that are not constants.");
                }
            }
        } else {
            throw new IllegalArgumentException("We can't analyze symbolizers with"
                    + "graphic collections.");
        }
    }

    /**
     * Create a new {@code ProportionalPoint}. As the associated analysis is
     * given in parameter, it is up to the calling method to be sure that
     * @param symbolizer
     * @param graphicLegend
     */
    public ProportionalPoint(PointSymbolizer symbolizer, ProportionalWKNLegend graphicLegend) {
        super(symbolizer);
        markGraphic = graphicLegend;
    }

    @Override
    public ConstantFormWKN getMarkGraphic() {
        return markGraphic;
    }

    public String getLookupFieldName(){
            return markGraphic.getLookupFieldName();
    }

    /**
     * Gets the data associated to the first interpolation point.
     * @return
     */
    public double getFirstData() {
        return markGraphic.getFirstData();
    }

    /**
     * Sets the data associated to the first interpolation point.
     * @return
     */
    public void setFirstData(double d) {
        markGraphic.setFirstData(d);
    }

    /**
     * Gets the data associated to the second interpolation point.
     * @return
     */
    public double getSecondData() {
        return markGraphic.getSecondData();
    }

    /**
     * Sets the data associated to the second interpolation point.
     * @return
     */
    public void setSecondData(double d) {
        markGraphic.setSecondData(d);
    }

    /**
     * Get the value of the first interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we retrieve directly the {@code double} it contains.
     * @return
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public double getFirstValue() throws ParameterException{
        return markGraphic.getFirstValue();
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
        markGraphic.setFirstValue(d);
    }

    /**
     * Get the value of the second interpolation point as a double. We are not
     * supposed to work here with {@code RealParameter} other than {@code
     * RealLiteral}, so we retrieve directly the {@code double} it contains.
     * @return
     * @throws ParameterException
     * If a problem is encountered while retrieving the double value.
     */
    public double getSecondValue() throws ParameterException{
        return markGraphic.getSecondValue();
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
        markGraphic.setSecondValue(d);
    }

    @Override
    public String getLegendTypeName() {
        return "Proportional Point";
    }

}
