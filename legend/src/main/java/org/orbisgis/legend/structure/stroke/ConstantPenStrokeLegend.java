/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.fill.ConstantFillLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;

/**
 * This class is used to represent instances of {@code PenStroke} whose
 * parameters (width, fill and dahs array) are all constant.
 * @author alexis
 */
public class ConstantPenStrokeLegend extends ConstantColorAndDashesPSLegend {

        /**
         * Build a new instance of {@code ConstantPenStrokeLegend}.
         * @param ps
         * @param width
         * @param fill
         * @param dash
         */
        public ConstantPenStrokeLegend(PenStroke ps, RealLiteralLegend width,
                        ConstantFillLegend fill, StringLiteralLegend dash) {
                super(ps, width, fill, dash);
        }

        /**
         * Get the width of the associated {@code PenStroke}.
         * @return
         */
        public double getLineWidth() {
            return ((RealLiteralLegend) getWidthAnalysis()).getDouble();
        }

        /**
         * Set the width of the associated {@code PenStroke}.
         * @param width
         */
        public void setLineWidth(double width) {
            ((RealLiteralLegend) getWidthAnalysis()).setDouble(width);
        }

        /**
         * Get the {@code String} that represent the dash pattern for the
         * associated {@code PenStroke}.
         * @return
         * The dash array in a string. Can't be null. Even if the underlying
         * {@code Symbolizer} does not have such an array, an empty {@code
         * String} is returned.
         */
        public String getDashArray() {
            String ret = "";
            StringLiteralLegend sll = (StringLiteralLegend) getDashAnalysis();
            if(sll == null){
                return ret;
            }
            StringLiteral lit = sll.getLiteral();
            if(lit != null){
                ret = lit.getValue(null, 0);
            }
            return ret == null ? "" : ret;
        }

        /**
        * Set the {@code String} that represent the dash pattern for the
         * associated {@code PenStroke}.
        * @param dashes
        */
        public void setDashArray(String str) {
            PenStroke ps = getPenStroke();
            StringLiteral rl = (StringLiteral) ps.getDashArray();
            String da = validateDashArray(str) ? str : "";
            if(!da.isEmpty()){
                if(rl == null){
                    rl = new StringLiteral(da);
                    ps.setDashArray(rl);
                    setDashAnalysis(new StringLiteralLegend(rl));
                } else {
                    rl.setValue(da);
                }
            } else {
                ps.setDashArray(null);
                setDashAnalysis(null);
            }
        }

        private boolean validateDashArray(String str) {
            String[] splits = str.split(" ");
            for(String s : splits){
                try{
                    double d = Double.valueOf(s);
                } catch(NumberFormatException nfe){
                    return false;
                }
            }
            return true;
        }
}
