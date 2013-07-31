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
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.graphic.ConstantFormWKN;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.thematic.ConstantFormPoint;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A {@code ProportionalPoint} is a {@link ConstantFormPoint} whose {@code
 * ViewBox} is defined so that it can be recognized as a {@code
 * MonovariateProportionalViewBox}.
 * @author Alexis Guéganno
 */
public class ProportionalPoint extends ConstantFormPoint
        implements IUniqueSymbolArea, LookupFieldName,
        IInterpolationLegend {

    private ProportionalWKNLegend markGraphic;
    private static final I18n I18N = I18nFactory.getI18n(ProportionalPoint.class);
    public static final String NAME = I18N.tr("Proportional Point");

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
     * @param pointSymbolizer
     * @throws IllegalArgumentException
     * If {@code pointSymbolizer} can't be recognized as a valid {@code
     * UniqueSymbolPoint}.
     */
    public ProportionalPoint(PointSymbolizer pointSymbolizer) {
        super(pointSymbolizer);
        if(pointSymbolizer.getGraphicCollection().getNumGraphics() == 1){
            Graphic gr = pointSymbolizer.getGraphicCollection().getGraphic(0);
            if(gr instanceof MarkGraphic){
                markGraphic = new ProportionalWKNLegend((MarkGraphic) gr);
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

    /**
     * Gets the Fill analysis embedded in this {@code IUniqueSymbolArea}.
     * @return
     */
    @Override
    public ConstantSolidFill getFillLegend() {
        return markGraphic.getSolidFill();
    }

    /**
     * Sets the Fill analysis embedded in this {@code IUniqueSymbolArea}.
     * @param csf
     */
    @Override
    public void setFillLegend(ConstantSolidFill csf) {
            markGraphic.setFillLegend(csf);
    }

    /**
     * Gets the analysis associated to the inner {@code PenStroke}.
     * @return
     */
    @Override
    public ConstantPenStroke getPenStroke(){
        return markGraphic.getPenStroke();
    }

    /**
     * Sets the analysis associated to the inner {@code PenStroke}.
     * @param cpsl
     */
    @Override
    public void setPenStroke(ConstantPenStroke cpsl) {
        markGraphic.setPenStroke(cpsl);
    }

    @Override
    public String getLookupFieldName(){
            return markGraphic.getLookupFieldName();
    }

    @Override
    public void setLookupFieldName(String name){
            markGraphic.setLookupFieldName(name);
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

    @Override
    public double getFirstValue() throws ParameterException{
        return markGraphic.getFirstValue();
    }
    
    @Override
    public void setFirstValue(double d) {
        markGraphic.setFirstValue(d);
    }

    @Override
    public double getSecondValue() throws ParameterException{
        return markGraphic.getSecondValue();
    }

    @Override
    public void setSecondValue(double d) {
        markGraphic.setSecondValue(d);
    }

    @Override
    public String getLegendTypeName() {
        return NAME;
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.proportional.ProportionalPoint";
    }

}
