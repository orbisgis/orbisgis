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
package org.orbisgis.legend.thematic.constant;

import java.util.List;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.MarkGraphicAnalyzer;
import org.orbisgis.legend.structure.graphic.ConstantFormWKN;
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.thematic.ConstantFormPoint;

/**
 * We are dealing with a simple point symbolizer. If we succeeded in recognizing
 * this thematic structure, we can say many things about the underlying
 * {@code PointSymbolize} :
 * <ul>
 * <li>It is a constant {@code MarkGraphic} instance, used with a well-known
 * name rather than an external Graphic.</li>
 * <li>Its contouring line is constant, made of a {@code PenStroke} with a
 * {@code SolidFill} of fixed colour and width.</li>
 * <li>It is filled with a {@code SolidFill} of fixed colour and opacity.</li>
 * <li>the width and height of its {@code ViewBox} are constant.</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class UniqueSymbolPoint extends ConstantFormPoint implements IUniqueSymbolArea {

    private ConstantWKNLegend markGraphic;

    /**
     * Build a new {@code UniqueSymbolPoint} from scratch. It will instanciate
     * the inner symbolizer as needed, as well as the underlying {@code LegendStructure}
     * structure.</p>
     * <p>We basically instanciate a new default {@code PointSymbolizer}, and
     * use it to compute our {@code LegendStructure} structure. That means we know
     * everything but the colour about the {@code Symbolizer}. An d we can be
     * sur it is a constant.
     */
    public UniqueSymbolPoint() {
        super(new PointSymbolizer());
        Graphic gr = ((PointSymbolizer) getSymbolizer()).getGraphicCollection().getGraphic(0);
        markGraphic = (ConstantWKNLegend) new MarkGraphicAnalyzer((MarkGraphic) gr).getLegend();
    }

    /**
     * Tries to build an instance of {@code UniqueSymbolPoint} using the given
     * {@code PointSymbolizer}.
     * @param pointSymbolizer
     * @throws IllegalArgumentException
     * If {@code pointSymbolizer} can't be recognized as a valid {@code
     * UniqueSymbolPoint}.
     */
    public UniqueSymbolPoint(PointSymbolizer pointSymbolizer)  {
        super(pointSymbolizer);
        //A UniqueSymbolPoint is mainly a constant and simple MarkGraphicLegend.
        if(pointSymbolizer.getGraphicCollection().getNumGraphics() == 1){
            Graphic gr = pointSymbolizer.getGraphicCollection().getGraphic(0);
            if(gr instanceof MarkGraphic){
                LegendStructure mgl = new MarkGraphicAnalyzer((MarkGraphic) gr).getLegend();
                if(mgl instanceof ConstantWKNLegend){
                    markGraphic = (ConstantWKNLegend) mgl;
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
     * Create a new {@code UniqueSymbolPoint}. As the associated analysis is
     * given in parameter, it is up to the calling method to be sure that
     * @param symbolizer
     * @param markGraphic
     */
    public UniqueSymbolPoint(PointSymbolizer symbolizer, ConstantWKNLegend markGraphic) {
        super(symbolizer);
        this.markGraphic = markGraphic;
    }

    @Override
    public ConstantFormWKN getMarkGraphic() {
        return markGraphic;
    }

    /**
     * Get the width of the {@code ViewBox} we are going to draw our symbol in.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxWidth(){
        return markGraphic.getViewBoxWidth();
    }

    /**
     * Get the height of the {@code ViewBox} we are going to draw our symbol in.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxHeight(){
        return markGraphic.getViewBoxHeight();
    }

    /**
     * Set the height of the {@code ViewBox} we are going to draw our symbol in.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxHeight(Double d) {
        markGraphic.setViewBoxHeight(d);
    }

    /**
     * Set the width of the {@code ViewBox} we are going to draw our symbol in.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxWidth(Double d) {
        markGraphic.setViewBoxWidth(d);
    }

    @Override
    public String getLegendTypeName() {
        return "Unique Symbol - Point";
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolPoint";
    }

    @Override
    public List<USParameter<?>> getParameters() {
        return USParameterFactory.getParameters(this);
    }

    @Override
    public List<USParameter<?>> getParametersArea() {
        return USParameterFactory.getParametersArea(this);
    }

    @Override
    public List<USParameter<?>> getParametersLine() {
        return USParameterFactory.getParametersLine(this);
    }
    
    public List<USParameter<?>> getParametersPoint() {
        return USParameterFactory.getParametersPoint(this);
    }

}
