/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.wrapper;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendsPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.IRulePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlRule;

/**
 * A Rule can be associated to a list of {@code Legend} instances. If
 * specialized enough, these legends will be associated to a dedicated  edition
 * panel. This class embeds this structure, as {@code Rule} instances are
 * associated to symbolizers, not to legends.
 * @author alexis
 */
public class RuleWrapper {

        private Rule rule;
        private List<ILegendPanel> legends;
        private PnlRule panel;

        /**
         * Build a {@code RuleWrapper} from scratch, with an empty list of 
         * {@code Legend} and an empty {@code Rule}.
         */
        public RuleWrapper(){
                legends = new ArrayList<ILegendPanel>();
                rule = new Rule();
        }

        /**
         * Build a {@code RuleWrapper} from scratch, with an empty list of
         * {@code Legend} and an empty {@code Rule}.
         * @param name
         *      The name of the rule.
         */
        public RuleWrapper(String name){
                this();
                rule.setName(name);
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
        public RuleWrapper(Rule r, List<ILegendPanel> l){
                rule = r;
                legends = l;
                List<Symbolizer> ls = r.getCompositeSymbolizer().getSymbolizerList();
                if(ls.size() != l.size()){
                        throw new IllegalArgumentException("The number of legends "
                                + "and of symbolizers mismatch");
                } else {
                        for(int i = 0; i<l.size(); i++){
                                if(l.get(i).getLegend().getSymbolizer() != ls.get(i)){
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
        public ILegendPanel getLegend(int i){
                return legends.get(i);
        }

        /**
         * Returns the index of {@code leg} in this {@code RuleWrapper}.
         * @param leg
         * @return
         * The index of the legend if it is emebedded in this wrapper, or -1
         * otherwise.
         */
        public int indexOf(ILegendPanel leg){
                return legends.indexOf(leg);
        }

        /**
         * Sets the ith {@code Legend} contained in this wrapper to {@code leg}.
         * @param i
         * @param leg
         * @throws IndexOutOfBoundsException if {@code i<0 || i>getSize() -1}.
         */
        public void setLegend(int i, ILegendPanel leg){
                legends.set(i, leg);
                rule.getCompositeSymbolizer().setSymbolizer(i, leg.getLegend().getSymbolizer());
        }

        /**
         * Removes leg from this {@code RuleWrapper};
         * @param leg
         */
        public void remove(ILegendPanel leg) {
                legends.remove(leg);
                rule.getCompositeSymbolizer().removeSymbolizer(leg.getLegend().getSymbolizer());
        }

        /**
         * Moves the ith registered {@code Legend} to position i-1.
         * @param rw
         */
        public void moveLegendUp(int i){
                if(i>0 && i<legends.size()){
                        rule.getCompositeSymbolizer().moveSymbolizerUp(legends.get(i).getLegend().getSymbolizer());
                        Collections.swap(legends, i, i-1);
                }
        }

        /**
         * Moves the ith registered {@code RuleWrapper} to position i+1.
         * @param rw
         */
        public void moveLegendDown(int i){
                if(i>=0 && i<legends.size()-1){
                        rule.getCompositeSymbolizer().moveSymbolizerDown(legends.get(i).getLegend().getSymbolizer());
                        Collections.swap(legends, i, i+1);
                }
        }

        /**
         * Adds {@code leg} at the end of the list of {@code Legend}s.
         * @param leg
         */
        public void addLegend(ILegendPanel leg){
                rule.getCompositeSymbolizer().addSymbolizer(leg.getLegend().getSymbolizer());
                legends.add(leg);
        }

        /**
         * Adds {@code leg} at the ith position in the list of
         * {@code Legend}s.
         * @param leg
         */
        public void addLegend(int i, ILegendPanel leg){
                legends.add(i, leg);
                rule.getCompositeSymbolizer().addSymbolizer(i, leg.getLegend().getSymbolizer());
        }

        /**
         * Checks that this {@code RuleWrapper} contains at least one {@code
         * Legend}.
         * @return
         */
        public boolean hasLegend() {
                for(ILegendPanel ilp : legends){
                        if(ilp.getLegend() != null){
                                return true;
                        }
                }
                return false;
        }

        /**
         * Get the panel that can be used to configure the {@code Rule}.
         * @return
         */
        public IRulePanel getPanel(){
                if(panel == null){
                        createRulePanel();
                }
                return panel;
        }

        /**
         * Gets the Id associated to the panel used to configure the inner
         * {@code Rule}.
         */
        public String getId(){
                return panel.getId();
        }

        private void createRulePanel(){
                panel = new PnlRule();
                panel.setRule(rule);
                panel.setId(LegendsPanel.getNewId());
        }

        List<String> validateInput() {
                LinkedList<String> ll = new LinkedList<String>();
                String ps = panel.validateInput();
                if(ps != null && !ps.isEmpty()){
                        ll.add(ps);
                }
                for(ILegendPanel ilp : legends){
                        String s = ilp.validateInput();
                        if(s!= null && !s.isEmpty()){
                                ll.add(s);
                        }
                }
                return ll;
        }

}
