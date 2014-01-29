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
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.view.toc.actions.cui.legends.ui.PnlStyle;

/**
 * A Symbolizer can be associated to legends through its {@code Rule}. However,
 * as {@code Rule}s are associated to {@code Symbolizer}, we must use {@code
 * RuleWrapper} instances rather than {@code Rule} instances. This class is
 * intended to make the link.
 * @author Alexis Guéganno
 */
public class StyleWrapper {

        private Style style;
        private List<RuleWrapper> ruleWrappers;
        private PnlStyle panel;

        public StyleWrapper(Style s, List<RuleWrapper> rw){
                ruleWrappers = rw;
                this.style=s;
                for (int i = 0; i < s.getRules().size(); i++) {
                        if(rw.get(i).getRule() != s.getRules().get(i)){
                                throw new IllegalArgumentException("Rules of the Style "
                                        + "must be the same that the wrapped ones !");
                        }
                }
        }

        /**
         * Get the ith {@code RuleWrapper} registered in this {@code
         * StyleWrapper}.
         * @param i
         * @return
         */
        public RuleWrapper getRuleWrapper(int i){
                return ruleWrappers.get(i);
        }

        /**
         * Gets the number of embedded {@code RuleWrapper} instances.
         * @return
         */
        public int getSize(){
                return ruleWrappers.size();
        }

        /**
         * Adds {@code rw} at the end of the list of {@code RuleWrapper}s.
         * @param rw
         */
        public void addRuleWrapper(RuleWrapper rw){
                style.addRule(rw.getRule());
                ruleWrappers.add(rw);
        }

        /**
         * Adds {@code rw} at the ith position in the list of 
         * {@code RuleWrapper}s.
         * @param rw
         */
        public void addRuleWrapper(int i, RuleWrapper rw){
                style.addRule(i, rw.getRule());
                ruleWrappers.add(i, rw);
        }


        /**
         * Sets the ith inner {@code RuleWrapper} to {@code rw}.
         * @param index
         * @param rw
         * @throws IndexOutOfBoundsException if {@code index<0 || index>getSize()-1}.
         */
        public void setRuleWrapper(int index, RuleWrapper rw){
                ruleWrappers.set(index, rw);
        }

        /**
         * Returns the index of rw in the list of embedded {@code RuleWrapper},
         * or -1 if this list does not contain the element.
         * @param rw
         * @return
         */
        public int indexOf(RuleWrapper rw){
                return ruleWrappers.indexOf(rw);
        }

        /**
         * Removes {@code rw} from this wrapper's children.
         * @param rw
         * @return
         */
        public void remove(RuleWrapper rw) {
                int i = indexOf(rw);
                style.deleteRule(i);
                ruleWrappers.remove(rw);
        }

        /**
         * Moves the ith registered {@code RuleWrapper} to position i-1.
         * @param rw
         */
        public void moveRuleWrapperUp(int i){
                if(i>0 && i<ruleWrappers.size()){
                        Collections.swap(ruleWrappers, i, i-1);
                        style.moveRuleUp(i);
                }
        }

        /**
         * Moves the ith registered {@code RuleWrapper} to position i+1.
         * @param rw
         */
        public void moveRuleWrapperDown(int i){
                if(i>=0 && i<ruleWrappers.size()-1){
                        Collections.swap(ruleWrappers, i, i+1);
                        style.moveRuleDown(i);
                }
        }

        public Style getStyle() {
                return style;
        }

        /**
         * Checks that the tree under this {@code StyleWrapper} contains at
         * least one {@code Legend}.
         * @return
         */
        public boolean hasLegend() {
                for(RuleWrapper rw : ruleWrappers){
                        if(rw.hasLegend()){
                                return true;
                        }
                }
                return false;
        }

        public List<String> validateInput() {
                LinkedList<String> ll= new LinkedList<String>();
                for(RuleWrapper rw :ruleWrappers){
                      ll.addAll(rw.validateInput());
                }
                return ll;
        }

        /**
         * Get the panel that can be used to configure the {@code Rule}.
         * @return
         */
        public PnlStyle getPanel(){
                if(panel == null){
                        createStylePanel();
                }
                return panel;
        }

        private void createStylePanel(){
                panel = new PnlStyle();
                panel.setStyle(style);
        }

}
