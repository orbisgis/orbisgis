/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.awt.Color;
import java.util.List;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.FillAnalyzer;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.thematic.ConstantStrokeArea;

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
 * @author alexis
 */
public class UniqueSymbolArea extends  ConstantStrokeArea implements IUniqueSymbolArea {

    private ConstantSolidFillLegend fillLegend;

    /**
     * Build a new default {@code UniqueSymbolArea} from scratch. It contains a
     * default {@code AreaSymbolizer}, which is consequently constant. The
     * associated {@code LegendStructure} structure is built during initialization.
     */
    public UniqueSymbolArea(){
        super();
        AreaSymbolizer symbolizer = (AreaSymbolizer) getSymbolizer();
        Fill fill = symbolizer.getFill();
        fillLegend = (ConstantSolidFillLegend) new FillAnalyzer(fill).getLegend();
    }

    /**
     * Build a new {@code UniqueSymbolArea} directly from the given {@code
     * AreaSymbolizer}.
     * @param symbolizer
     */
    public UniqueSymbolArea(AreaSymbolizer symbolizer){
        super(symbolizer);
        //If we're here, we have a constant stroke : it is either null or an instance
        //of ConstantPenStrokeLegend. Let's analyze the Fill.
        Fill fill = symbolizer.getFill();
        if(fill != null){
            LegendStructure fillLgd = new FillAnalyzer(fill).getLegend();
            if(fillLgd instanceof ConstantSolidFillLegend){
                fillLegend = (ConstantSolidFillLegend) fillLgd;
            } else {
                throw new IllegalArgumentException("The fill of this AreaSymbolizer "
                        + "can't be recognized as constant.");
            }
        }
        //If we're here, we have a constant stroke and a constant fill. If we
        //can't manage the input Symbolizer, an exception has been thrown.
    }


    /**
     * A {@code UniqueSymbolArea} is associated to a {@code SolidFill}, that
     * is filled using a given {@code Color}.
     * @return
     */
    @Override
    public Color getFillColor(){
        return fillLegend.getColor();
    }

    /**
     * Set the {@code Color} that will be used to fill the {@code SolidFill}.
     * @param col
     */
    @Override
    public void setFillColor(Color col){
        fillLegend.setColor(col);
    }

    @Override
    public String getLegendTypeName() {
        return "Unique Symbol - Polygon";
    }

    @Override
    public List<USParameter<?>> getParameters() {
        return USParameterFactory.getParameters(this);
    }
    
    @Override
    public List<USParameter<?>> getParametersLine() {
        return USParameterFactory.getParametersLine(this);
    }

    @Override
    public List<USParameter<?>> getParametersArea() {
        return USParameterFactory.getParametersArea(this);
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolArea";
    }
    
}
