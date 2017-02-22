/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.legend.analyzer.symbolizers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.coremap.renderer.se.visitors.FeaturesVisitor;
import org.orbisgis.coremap.renderer.se.visitors.UsedAnalysisVisitor;
import org.orbisgis.legend.AbstractAnalyzer;

/**
 * Basic analyzer for symbolizer nodes. We check the nature of inner {@code
 * SeParameter} instances. We try to prove that :
 * <ul><li>We just need one field to use the node</li>
 * <li>All the analysis used in the node are of the same type</li>
 * <li>None of the analysis is dependant upon another one.</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class ParametersAnalyzer extends AbstractAnalyzer {

        private UsedAnalysis ua = new UsedAnalysis();
        private Set<String> fields = new HashSet<String>();

        /**
         * Analyses the given {@link SymbolizerNode} instance. It will get its
         * {@code UsedAnalysis} and set of needed fields so that we can
         * determine if :</p>
         * <ul><li>It contains nested analysis<li>
         * <li>It contains analysis made on different fields</li>
         * <li>There is only one type of analysis used</li></ul>
         * @param sn
         */
        public void analyzeParameters(SymbolizerNode sn){
                UsedAnalysisVisitor uav = new UsedAnalysisVisitor();
                sn.acceptVisitor(uav);
                ua = uav.getUsedAnalysis();
                FeaturesVisitor fv = new FeaturesVisitor();
                sn.acceptVisitor(fv);
                fields = fv.getResult();
        }

        /**
         * Returns true if and only if there is only one type of analysis
         * referenced in the inner {@link UsedAnalysis}.
         * @return
         */
        public boolean isAnalysisUnique(){
                if(ua.isInterpolateUsed()){
                        return !ua.isCategorizeUsed() && !ua.isRecodeUsed();
                } else {
                        return !ua.isCategorizeUsed() || !ua.isRecodeUsed();
                }
        }

        /**
         * Returns true if and only if the found analysis does not contain other
         * ones as parameter.
         * @return
         */
        public boolean isAnalysisLight(){
                List<SeParameter> l = ua.getAnalysis();
                UsedAnalysisVisitor uav = new UsedAnalysisVisitor();
                for (SeParameter seParameter : l) {
                        seParameter.acceptVisitor(uav);
                        if(uav.getUsedAnalysis().getAnalysis().size()>1){
                                return false;
                        }
                }
                return true;
        }

        /**
         * Returns true if and only if there is only one needed field for all
         * the referenced analysis.
         * @return
         */
        public boolean isFieldUnique(){
                return fields.size() <=1;
        }

        /**
         * Gets the compilation of all the analysis that have been made in the
         * last analyzed SymbolizerNode.
         * @return
         */
        public UsedAnalysis getUsedAnalysis(){
                return ua;
        }

        /**
         * Get a String text explaining the status of the currently analyzed SymbolizerNode.
         * @return
         */
        public String getStatus(){
            StringBuilder sb = new StringBuilder();
            sb.append("Analysis unicity : ").append(isAnalysisUnique()).append("\n");
            sb.append("Light analysis : ").append(isAnalysisUnique()).append("\n");
            sb.append("Field unicity : ").append(isAnalysisUnique()).append("\n");
            return sb.toString();
        }
}
