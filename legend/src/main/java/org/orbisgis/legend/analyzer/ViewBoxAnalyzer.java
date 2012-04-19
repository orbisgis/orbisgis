/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.legend.analyzer.parameter.RealParameterAnalyzer;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.NumericLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.viewbox.*;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code ViewBox}
 * instances. It's mainly chasing monovariate and bivariate symbols, used as
 * proportional symbols.
 * @author alexis
 */
public class ViewBoxAnalyzer  extends AbstractAnalyzer {

        private ViewBox vb;

        /**
         * Build a new {@code ViewBoxAnalyzer}. It uses the given {@code
         * ViewBox}. The analysis is done during the initialization, and is
         * supposed to create a {@code DefaultViewBox}, or, if in a better known
         * configuration, one of its specialization.
         * @param view
         */
        public ViewBoxAnalyzer(ViewBox view){
                vb = view;
                analyzeViewBox();
        }

        private void analyzeViewBox(){
                RealParameter height = vb.getHeight();
                RealParameter width = vb.getWidth();
                //we try to start our analysis from height - if it's null, we'll
                //begin with width. If both are null, we have a problem, IMHO.
                if(height != null){
                        if(width != null){
                                heightAndWidth(height, width);
                        } else {
                                oneDimOnly(height, true);
                        }
                } else {
                        if(width != null){
                                oneDimOnly(width, false);
                        } else {
                                throw new IllegalArgumentException("Both dimensions"
                                        + "of this ViewBox CAN'T be null !");
                        }
                }

        }

        /**
         * If using this method, we suppose both of the arguments are not null.
         * @param height
         * @param width
         */
        private void heightAndWidth(RealParameter height, RealParameter width) {
                RealParameterAnalyzer rph = new RealParameterAnalyzer(height);
                LegendStructure han = rph.getLegend();
                RealParameterAnalyzer rpw = new RealParameterAnalyzer(width);
                LegendStructure wan = rpw.getLegend();
                if(han instanceof LinearInterpolationLegend){
                        LinearInterpolationLegend hli = (LinearInterpolationLegend) han;
                        if(wan instanceof LinearInterpolationLegend){
                                LinearInterpolationLegend wli = (LinearInterpolationLegend) wan;
                                BivariateProportionalViewBox bvb = new BivariateProportionalViewBox(hli, wli, vb);
                                setLegend(bvb);
                        } else if(wan instanceof RealLiteralLegend){
                                RealLiteralLegend wrl = (RealLiteralLegend) wan;
                                MonovariateLinearVB mlv = new MonovariateLinearVB(hli, wrl, vb, true);
                                setLegend(mlv);
                        }
                } else if(wan instanceof LinearInterpolationLegend &&
                                han instanceof RealLiteralLegend){
                        LinearInterpolationLegend wli = (LinearInterpolationLegend) wan;
                        RealLiteralLegend hrl = (RealLiteralLegend) han;
                        MonovariateLinearVB mlv = new MonovariateLinearVB(hrl, wli, vb, false);
                        setLegend(mlv);
                } else if(wan instanceof RealLiteralLegend && han instanceof RealLiteralLegend){
                        ConstantViewBox cvb = new ConstantViewBox((RealLiteralLegend) han, (RealLiteralLegend) wan, vb);
                        setLegend(cvb);
                } else {
                        DefaultViewBox dvb = new DefaultViewBox((NumericLegend)han, (NumericLegend)wan, vb);
                        setLegend(dvb);
                }
        }

        private void oneDimOnly(RealParameter dimension, boolean onheight){
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(dimension);
                LegendStructure ld = rpa.getLegend();
                if(ld instanceof SqrtInterpolationLegend){
                        SqrtInterpolationLegend sqil = (SqrtInterpolationLegend) ld;
                        MonovariateProportionalViewBox mpv = new MonovariateProportionalViewBox(sqil, onheight, vb);
                        setLegend(mpv);
                } else if(ld instanceof RealLiteralLegend){
                        ConstantViewBox mpv = new ConstantViewBox((RealLiteralLegend) ld, onheight, vb);
                        setLegend(mpv);

                }
        }

}
