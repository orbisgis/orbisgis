/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.util.List;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.ConstantColorAndDashesLine;

/**
 * Represents a {@code LineSymbolizer} whose parameters are constant, whatever
 * the input data are. We expect from it :
 * <ul>
 * <li>To be defined with a {@code PenStroke} </li>
 * <li>To have a constant dash array structure</li>
 * <li>To have a constant width</li>
 * <li>To be filled with a constant {@code SolidFill}, and consequently with a
 * constant {@code Color}.</li>
 * </ul>
 * @author alexis
 */
public class UniqueSymbolLine extends ConstantColorAndDashesLine implements IUniqueSymbolLine {

    private ConstantPenStrokeLegend strokeLegend;

    /**
     * Build a new default {@code UniqueSymbolLine} from scratch. It contains a
     * default {@code LineSymbolizer}, which is consequently constant. The
     * associated {@code LegendStructure} structure is built during initialization.
     */
    public UniqueSymbolLine() {
        super(new LineSymbolizer());
        Stroke gr = ((LineSymbolizer)getSymbolizer()).getStroke();
        strokeLegend = (ConstantPenStrokeLegend) new PenStrokeAnalyzer((PenStroke) gr).getLegend();
    }

    /**
     * Build a new {@code UniqueSymbolLine} from the given symbolizer. Note that
     * {@code symbolizer} must really be a unique symbole. Otherwise, an
     * {@code IllegalArgumentException} will be thrown.
     * @param symbolizer
     * @throws IllegalArgumentException
     * If the {@code Stroke} contaiend in {@code symbolizer} can't be recognized
     * as a {@code ConstantPenStrokeLegend}.
     */
    public UniqueSymbolLine(LineSymbolizer symbolizer) {
        super(symbolizer);
        Stroke gr = ((LineSymbolizer)getSymbolizer()).getStroke();
        if(gr instanceof PenStroke){
            LegendStructure mgl = new PenStrokeAnalyzer((PenStroke) gr).getLegend();
            if(mgl instanceof ConstantPenStrokeLegend){
                strokeLegend = (ConstantPenStrokeLegend) mgl;
            }  else {
                throw new IllegalArgumentException("A unique symbol must be a  "
                        + "constant.");
            }
        }
    }

    /**
     * Build a new {@code UniqueSymbolLine} instance from the given symbolizer
     * and legend. As the inner analysis is given directly with the symbolizer,
     * we won't check they match. It is up to the caller to check they do.
     * @param symbolizer
     * @param legend
     */
    public UniqueSymbolLine(LineSymbolizer symbolizer, ConstantPenStrokeLegend legend) {
        super(symbolizer);
        strokeLegend = legend;
    }

    /**
     * Get the {@code LegendStructure} associated to this {@code UniqueSymbolLine}.
     * @return
     */
    @Override
    public LegendStructure getStrokeLegend() {
        return strokeLegend;
    }

    /**
     * Get the width of the lines to be drawn.
     * @return
     */
    @Override
    public Double getLineWidth() {
        return strokeLegend.getLineWidth();
    }

    /**
     * Set the width of the lines to be drawn.
     * @param d
     */
    @Override
    public void setLineWidth(Double d) {
        strokeLegend.setLineWidth(d);
    }

    /**
     * Get the {@code String} that represent the dash pattern for this unique
     * symbol. It is made of double values separated by spaces, stored in a
     * String...
     * @return
     */
    @Override
    public String getDashArray(){
        return strokeLegend.getDashArray();
    }

    /**
     * Set the {@code String} that represent the dash pattern for this unique
     * symbol. It must be made of double values separated by spaces, stored in a
     * String...
     * @param dashes
     */
    @Override
    public void setDashArray(String dashes){
        strokeLegend.setDashArray(dashes);
    }

    @Override
    public String getLegendTypeName() {
        return "Unique Symbol - Line";
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.constant.UniqueSymbolLine";
    }

    @Override
    public List<USParameter<?>> getParameters() {
        return USParameterFactory.getParameters(this);
    }
}
