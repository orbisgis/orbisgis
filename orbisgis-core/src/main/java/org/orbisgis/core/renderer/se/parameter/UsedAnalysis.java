package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to retrieve all the analysis that are made in a SE style.
 * All the nodes of the tree are asked if an analysis is used. If it is, the
 * matching boolean is set to true here. In the end, the symbolizer can know
 * which analysis are used.
 * @author alexis
 */
public class UsedAnalysis {

        private boolean categorizeUsed = false;
        private boolean interpolateUsed = false;
        private boolean recodeUsed = false;
        private List<SeParameter> analysis = new ArrayList<SeParameter>();

        /**
         * If true, a recode has been found in the SE tree.
         * @return
         */
        public boolean isRecodeUsed() {
                return recodeUsed;
        }

        /**
         * If true, a recode has been found in the SE tree.
         * @return
         */
        public boolean isCategorizeUsed() {
                return categorizeUsed;
        }

        /**
         * If true, an interpolate has been found in the SE tree.
         * @return
         */
        public boolean isInterpolateUsed() {
                return interpolateUsed;
        }

        /**
         * Includes the given SeParameter. For instance, if it is an instance of
         * {@code Interpolate}, it will set {@code interpolateUsed} to {@code
         * true}. If it is not, it will let {@code interpolateUsed} unchanged.
         * </p>
         * <p>{@code rp} is added to the list of analysis if and only if it is
         * not a literal.
         * @param rp
         */
        public void include(SeParameter rp){
                if(rp instanceof Interpolate){
                        interpolateUsed = true;
                        analysis.add(rp);
                }
                if(rp instanceof Recode){
                        recodeUsed = true;
                        analysis.add(rp);
                }
                if(rp instanceof Categorize){
                        categorizeUsed = true;
                        analysis.add(rp);
                }
        }

        /**
         * Merge this {@code UsedAnalysis} with {@code other}. Each analysis
         * will be considered {@code true} if {@code true} in {@code this} or in
         * {@code other}. Moreover, the two lists of analysis are merged too.
         * @param other
         */
        public void merge(UsedAnalysis other){
                recodeUsed = recodeUsed || other.recodeUsed;
                categorizeUsed = categorizeUsed || other.categorizeUsed;
                interpolateUsed = interpolateUsed || other.interpolateUsed;
                analysis.addAll(other.analysis);
        }

        /**
         * Get the list of {@code SeParameter} that have been recognized during
         * the feeding of this {@code UsedAnalysis} instance.
         * @return
         */
        public List<SeParameter> getAnalysis(){
                return analysis;
        }

}
