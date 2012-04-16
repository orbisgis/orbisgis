/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;

/**
 * A Rule can be associated to a list of {@code Legend} instances. If
 * specialized enough, these legends will be associated to a dedicated  edition
 * panel. This class embeds this structure, as {@code Rule} instances are
 * associated to symbolizers, not to legends.
 * @author alexis
 */
public class RuleWrapper {

        private Rule rule;
        private List<Legend> legends;

        /**
         * Build a {@code RuleWrapper} from scratch, with an empty list of 
         * {@code Legend} and an empty {@code Rule}.
         */
        public RuleWrapper(){
                legends = new ArrayList<Legend>();
                rule = new Rule();
        }

        /**
         * Build a new {@code RuleWrapper} using the given {@code Rule} and list
         * of {@code Legend}. If the symbolizers contained in {@code r} and in
         * {@code l} don't match, an exception is thrown.
         * @param r
         * @param l
         * @throws IllegalArgumentException if the symbolizers contained in the
         * {@code Legend} instances and in the {@code Rule} mismatch.
         */
        public RuleWrapper(Rule r, List<Legend> l){
                rule = r;
                legends = l;
                List<Symbolizer> ls = r.getCompositeSymbolizer().getSymbolizerList();
                if(ls.size() != l.size()){
                        throw new IllegalArgumentException("The number of legends "
                                + "and of symbolizers mismatch");
                } else {
                        for(int i = 0; i<l.size(); i++){
                                if(l.get(i).getSymbolizer() != ls.get(i)){
                                        throw new IllegalArgumentException("Symbolizers registered"
                                                + "in the rule and in the legend mismatch.");
                                }
                        }
                }
        }

        /**
         * Gets the {@code Rule} embedded in this {@code RuleWrapper}.
         * @return
         */
        public Rule getRule() {
                return rule;
        }

        /**
         * Gets the number of {@code Legend} instances associated to this {@code
         * RuleWrapper}.
         * @return
         */
        public int getSize(){
                return legends.size();
        }

        /**
         * Gets the ith {@code Legend} associated to this wrapper.
         * @param i
         * @return
         * @throws IndexOutOfBoundsException if {@code i<0 && i>= getSize()}.
         */
        public Legend getLegend(int i){
                return legends.get(i);
        }

        /**
         * Returns the index of {@code leg} in this {@code RuleWrapper}.
         * @param leg
         * @return
         * The index of the legend if it is emebedded in this wrapper, or -1
         * otherwise.
         */
        public int indexOf(Legend leg){
                return legends.indexOf(leg);
        }

        /**
         * Sets the ith {@code Legend} contained in this wrapper to {@code leg}.
         * @param i
         * @param leg
         * @throws IndexOutOfBoundsException if {@code i<0 || i>getSize() -1}.
         */
        public void setLegend(int i, Legend leg){
                legends.set(i, leg);
                rule.getCompositeSymbolizer().setSymbolizer(i, leg.getSymbolizer());
        }

        /**
         * Removes leg from this {@code RuleWrapper};
         * @param leg
         */
        public void remove(Legend leg) {
                legends.remove(leg);
                rule.getCompositeSymbolizer().removeSymbolizer(leg.getSymbolizer());
        }

        /**
         * Moves the ith registered {@code Legend} to position i-1.
         * @param rw
         */
        public void moveLegendUp(int i){
                if(i>0 && i<legends.size()){
                        rule.getCompositeSymbolizer().moveSymbolizerUp(legends.get(i).getSymbolizer());
                        Collections.swap(legends, i, i-1);
                }
        }

        /**
         * Moves the ith registered {@code RuleWrapper} to position i+1.
         * @param rw
         */
        public void moveLegendDown(int i){
                if(i>=0 && i<legends.size()-1){
                        rule.getCompositeSymbolizer().moveSymbolizerDown(legends.get(i).getSymbolizer());
                        Collections.swap(legends, i, i+1);
                }
        }

        /**
         * Adds {@code rw} at the end of the list of {@code RuleWrapper}s.
         * @param leg
         */
        public void addRuleWrapper(Legend leg){
                rule.getCompositeSymbolizer().addSymbolizer(leg.getSymbolizer());
                legends.add(leg);
        }

        /**
         * Adds {@code rw} at the ith position in the list of
         * {@code RuleWrapper}s.
         * @param leg
         */
        public void addRuleWrapper(int i, Legend leg){
                legends.add(i, leg);
                rule.getCompositeSymbolizer().addSymbolizer(i, leg.getSymbolizer());
        }

}
