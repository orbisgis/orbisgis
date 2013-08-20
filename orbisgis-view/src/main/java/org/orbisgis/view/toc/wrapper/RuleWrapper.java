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
package org.orbisgis.view.toc.wrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.view.toc.actions.cui.SimpleStyleEditor;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.ui.PnlRule;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A Rule can be associated to a list of {@code Legend} instances. If
 * specialized enough, these legends will be associated to a dedicated edition
 * panel. This class embeds this structure, as {@code Rule} instances are
 * associated to symbolizers, not to legends.
 * @author Alexis Guéganno
 */
public class RuleWrapper {

        private static final I18n I18N = I18nFactory.getI18n(RuleWrapper.class);

        private Rule rule;
        private List<ILegendPanel> legends;
        private PnlRule panel;

        private SimpleStyleEditor editor;

        /**
         * Build a new {@code RuleWrapper} using the given {@code Rule} and list
         * of {@code ILegendPanel}s. If the symbolizers contained in {@code r} and in
         * the corresponding {@code Legend}s of {@code l} don't match, an
         * exception is thrown. The SimpleStyleEditor is used for initializing
         * a new PnlRule in {@link #createRulePanel()}.
         *
         * @param ed SimpleStyleEditor
         * @param r  Rule
         * @param l  List of ILegendPanels
         *
         * @throws IllegalArgumentException if the symbolizers contained in the
         * {@code ILegendPanel} instances and in the {@code Rule} don't match.
         */
        public RuleWrapper(SimpleStyleEditor ed, Rule r, List<ILegendPanel> l) {
            editor = ed;
            rule = r;
            legends = l;
            List<Symbolizer> ls = r.getCompositeSymbolizer().getSymbolizerList();
            if (ls.size() != l.size()) {
                throw new IllegalArgumentException("The number of legends "
                        + "and of symbolizers mismatch");
            } else {
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i).getLegend().getSymbolizer() != ls.get(i)) {
                        throw new IllegalArgumentException("Symbolizers registered "
                                + "in the rule and in the legend mismatch.");
                    }
                }
            }
            createRulePanel();
        }

        /**
         * Gets the {@code Rule} embedded in this {@code RuleWrapper}.
         *
         * @return
         */
        public Rule getRule() {
                return rule;
        }

        /**
         * Gets the number of {@code Legend} instances associated to this {@code
         * RuleWrapper}.
         *
         * @return
         */
        public int getSize() {
                return legends.size();
        }

        /**
         * Gets the ith {@code Legend} associated to this wrapper.
         *
         * @param i
         * @return
         * @throws IndexOutOfBoundsException if {@code i<0 && i>= getSize()}.
         */
        public ILegendPanel getLegend(int i) {
                return legends.get(i);
        }

        /**
         * Returns the index of {@code leg} in this {@code RuleWrapper}.
         *
         * @param leg
         * @return The index of the legend if it is emebedded in this wrapper,
         * or -1 otherwise.
         */
        public int indexOf(ILegendPanel leg) {
                return legends.indexOf(leg);
        }

        /**
         * Sets the ith {@code Legend} contained in this wrapper to {@code leg}.
         *
         * @param i
         * @param leg
         * @throws IndexOutOfBoundsException if {@code i<0 || i>getSize() -1}.
         */
        public void setLegend(int i, ILegendPanel leg) {
                legends.set(i, leg);
                rule.getCompositeSymbolizer().setSymbolizer(i, leg.getLegend().getSymbolizer());
        }

        /**
         * Removes leg from this {@code RuleWrapper};
         *
         * @param leg
         */
        public void remove(ILegendPanel leg) {
                legends.remove(leg);
                rule.getCompositeSymbolizer().removeSymbolizer(leg.getLegend().getSymbolizer());
        }

        /**
         * Moves the ith registered {@code Legend} to position i-1.
         *
         * @param i the index of the legend to be moved
         */
        public void moveLegendUp(int i) {
                if (i > 0 && i < legends.size()) {
                        rule.getCompositeSymbolizer().moveSymbolizerUp(legends.get(i).getLegend().getSymbolizer());
                        Collections.swap(legends, i, i - 1);
                }
        }

        /**
         * Moves the ith registered {@code RuleWrapper} to position i+1.
         *
         * @param i the index of the legend to be moved
         */
        public void moveLegendDown(int i) {
                if (i >= 0 && i < legends.size() - 1) {
                        rule.getCompositeSymbolizer().moveSymbolizerDown(legends.get(i).getLegend().getSymbolizer());
                        Collections.swap(legends, i, i + 1);
                }
        }

        /**
         * Adds {@code leg} at the end of the list of {@code Legend}s.
         *
         * @param leg
         */
        public void addLegend(ILegendPanel leg) {
                rule.getCompositeSymbolizer().addSymbolizer(leg.getLegend().getSymbolizer());
                legends.add(leg);
        }

        /**
         * Adds {@code leg} at the ith position in the list of
         * {@code Legend}s.
         *
         * @param leg
         */
        public void addLegend(int i, ILegendPanel leg) {
                legends.add(i, leg);
                rule.getCompositeSymbolizer().addSymbolizer(i, leg.getLegend().getSymbolizer());
        }

        /**
         * Checks that this {@code RuleWrapper} contains at least one {@code
         * Legend}.
         *
         * @return
         */
        public boolean hasLegend() {
                for (ILegendPanel ilp : legends) {
                        if (ilp.getLegend() != null) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Get the panel that can be used to configure the {@code Rule}.
         *
         * @return
         */
        public PnlRule getPanel() {
                if (panel == null) {
                        createRulePanel();
                }
                return panel;
        }

        /**
         * Gets the Id associated to the panel used to configure the inner
         * {@code Rule}.
         */
        public String getId() {
                return panel.getId();
        }

        private void createRulePanel() {
                panel = new PnlRule(editor);
                panel.setRule(rule);
        }

        List<String> validateInput() {
                LinkedList<String> ll = new LinkedList<String>();
                String ps = panel.validateInput();
                ll.add(ps);
                for (ILegendPanel ilp : legends) {
                        String s = ilp.validateInput();
                        ll.add(s);
                }
            Double max = rule.getMaxScaleDenom();
            Double min = rule.getMinScaleDenom();
            boolean err = max != null && min != null && min > max;
            if(err){
                ll.add(I18N.tr("Min scale greater than max scale in rule {0}.", rule.getName()));
            }
            return ll;
        }
}
