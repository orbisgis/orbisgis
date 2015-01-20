/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legend;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.orbisgis.sif.components.resourceTree.AbstractTreeModel;
import org.orbisgis.view.toc.wrapper.RuleWrapper;
import org.orbisgis.view.toc.wrapper.StyleWrapper;

/**
 * This tree model is used in the legend edition panel. Its depth is only 2 :
 * <ul>
 * <li>Level 0 (root) : the Style we are editing.</li>
 * <li>Level 1 : the Rules</li>
 * <li>Level 2 : the Legends</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class LegendTreeModel extends AbstractTreeModel {

        private StyleWrapper root;

        public LegendTreeModel(JTree tree, StyleWrapper r){
                super(tree);
                root = r;
        }

        public void refresh(){
                //This is way too violent : it would be faster to create a
                //dedicated event for each change !
                fireEvent();
        }

        @Override
        public Object getRoot() {
                return root;
        }

        @Override
        public Object getChild(Object parent, int index) {
                if(parent instanceof StyleWrapper){
                        return getChild((StyleWrapper) parent, index);
                } else if(parent instanceof RuleWrapper){
                        return getChild((RuleWrapper)parent, index);
                }
                return null;
        }

        @Override
        public int getChildCount(Object parent) {
                if(parent instanceof StyleWrapper){
                        return ((StyleWrapper)parent).getSize();
                } else if(parent instanceof RuleWrapper){
                        return ((RuleWrapper) parent).getSize();
                } else {
                        return -1;
                }
        }

        @Override
        public boolean isLeaf(Object node) {
                return node instanceof ILegendPanel;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
                if(parent instanceof StyleWrapper && child instanceof RuleWrapper){
                        return getIndexOfChild((StyleWrapper) parent, (RuleWrapper) child);
                } else if(parent instanceof RuleWrapper && child instanceof ILegendPanel){
                        return getIndexOfChild((RuleWrapper) parent, (ILegendPanel) child);
                } else {
                        return -1;
                }
        }

        /**
         * Add {@code newElt} in {@code parent}.
         * @param parent
         * @param newElt
         *      The object to be inserted. If {@code parent} is a {@code
         *      StyleWrapper}, must be a {@code RuleWrapper}. If {@code parent}
         *      is a {@code RuleWrapper}, must be a {@code Legend}.
         * @param current
         *      The object after which we will inser newElt, if possible. As it
         *      will be searched for in parent, it must be of the same type as
         *      newElt.
         */
        public void addElement(Object parent, Object newElt, Object current){
                if(parent instanceof RuleWrapper && newElt instanceof ILegendPanel){
                        if(current instanceof ILegendPanel){
                                addElement((RuleWrapper) parent, (ILegendPanel) newElt, (ILegendPanel) current);
                        } else {
                                addElement((RuleWrapper) parent, (ILegendPanel) newElt, null);
                        }
                } else if(parent instanceof StyleWrapper && newElt instanceof RuleWrapper){
                        if(current instanceof RuleWrapper){
                                addElement((RuleWrapper) newElt, (RuleWrapper) current);
                        } else {
                                addElement((RuleWrapper) newElt, null);
                        }
                }
        }

        /**
         * Remove {@code oldElt} from {@code parent}.
         * @param parent
         * @param oldElt
         *      The object to be removed. If {@code parent} is a {@code
         *      StyleWrapper}, must be a {@code RuleWrapper}. If {@code parent}
         *      is a {@code RuleWrapper}, must be a {@code Legend}.
         */
        public void removeElement(Object parent, Object oldElt){
                if(parent instanceof RuleWrapper && oldElt instanceof ILegendPanel){
                        removeElement((RuleWrapper) parent, (ILegendPanel) oldElt);
                } else if(parent instanceof StyleWrapper && oldElt instanceof RuleWrapper){
                        removeElement((RuleWrapper) oldElt);
                }
        }

        private void removeElement(RuleWrapper rw, ILegendPanel old){
                int pos = rw.indexOf(old);
                if(pos >=0){
                        rw.remove(old);
                        TreeModelEvent tme = new TreeModelEvent(
                                this,
                                new Object[]{root, rw},
                                new int[]{pos},
                                new Object[]{old});
                        fireNodeRemoved(tme);
                }
        }

        private void removeElement(RuleWrapper rw){
                int pos = root.indexOf(rw);
                if(pos >=0){
                        root.remove(rw);
                        TreeModelEvent tme = new TreeModelEvent(
                                this,
                                new Object[]{root},
                                new int[]{pos},
                                new Object[]{rw});
                        fireNodeRemoved(tme);
                }
        }

        /**
         * Move {@code elt} down in {@code parent}. If {@code elt} is a legend,
         * it is moved in the underlying RuleWrapper. If it is a RuleWrapper, it
         * is moved in the underlying StyleWrapper.
         * @param parent
         * @param elt
         *      The element we want to move down. If {@code parent} is a {@code
         *      StyleWrapper}, must be a {@code RuleWrapper}. If {@code parent}
         *      is a {@code RuleWrapper}, must be a {@code Legend}.
         */
        public void moveElementDown(Object parent, Object elt){
                if(parent instanceof RuleWrapper && elt instanceof ILegendPanel){
                        moveElementDown((RuleWrapper) parent, (ILegendPanel) elt);
                } else if(parent instanceof StyleWrapper && elt instanceof RuleWrapper){
                        moveElementDown((RuleWrapper) elt);
                }
        }

        /**
         * Move {@code elt} up in {@code parent}. If {@code elt} is a legend,
         * it is moved in the underlying RuleWrapper. If it is a RuleWrapper, it
         * is moved in the underlying StyleWrapper.
         * @param parent
         * @param elt
         *      The element we want to move up. If {@code parent} is a {@code
         *      StyleWrapper}, must be a {@code RuleWrapper}. If {@code parent}
         *      is a {@code RuleWrapper}, must be a {@code Legend}.
         */
        public void moveElementUp(Object parent, Object elt){
                if(parent instanceof RuleWrapper && elt instanceof ILegendPanel){
                        moveElementUp((RuleWrapper) parent, (ILegendPanel) elt);
                } else if(parent instanceof StyleWrapper && elt instanceof RuleWrapper){
                        moveElementUp((RuleWrapper) elt);
                }
        }

        /**
         * Checks that there is at least one {@code Legend} under this {@code
         * TreeModel}.
         * @return
         */
        public boolean hasLegend(){
                return root.hasLegend();
        }

        private void addElement(RuleWrapper newElt, RuleWrapper current){
                int pos;
                if(current == null){
                        pos = root.getSize();
                } else {
                        pos = root.indexOf(current)+1;
                        pos = pos == -1 ? root.getSize() : pos;
                }
                root.addRuleWrapper(pos, newElt);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root},
                        new int[]{pos},
                        new Object[]{newElt});
                fireNodeInserted(tme);
        }

        private void addElement(RuleWrapper parent, ILegendPanel newElt, ILegendPanel current){
                int pos;
                if(current == null){
                        pos = parent.getSize();
                } else {
                        pos = parent.indexOf(current)+1;
                        pos = pos == -1 ? parent.getSize() : pos;
                }
                parent.addLegend(pos, newElt);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root,parent},
                        new int[]{pos},
                        new Object[]{newElt});
                fireNodeInserted(tme);
        }

        /**
         * Manage element moves for legend nodes
         * @param rw
         * @param l
         */
        private void moveElementDown(RuleWrapper rw, ILegendPanel l){
                int i = rw.indexOf(l);
                rw.moveLegendDown(i);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root,rw});
                fireStructureChanged(tme);
        }

        /**
         * Manage element moves for RuleWrapper nodes.
         * @param rw
         */
        private void moveElementDown(RuleWrapper rw){
                int i = getIndexOfChild(root, rw);
                root.moveRuleWrapperDown(i);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root});
                fireStructureChanged(tme);
        }

        private void moveElementUp(RuleWrapper rw, ILegendPanel l){
                int i = rw.indexOf(l);
                rw.moveLegendUp(i);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root,rw});
                fireStructureChanged(tme);
        }

        private void moveElementUp(RuleWrapper rw) {
                int i = getIndexOfChild(root, rw);
                root.moveRuleWrapperUp(i);
                TreeModelEvent tme = new TreeModelEvent(
                        this,
                        new Object[]{root});
                fireStructureChanged(tme);
        }

        private RuleWrapper getChild(StyleWrapper s, int i){
                if(i>=0 && i < s.getSize()){
                        return s.getRuleWrapper(i);
                }
                return null;
        }

        private ILegendPanel getChild(RuleWrapper r, int i){
                if(i>=0 && i<r.getSize()){
                        return r.getLegend(i);
                }
                return null;
        }

        private int getIndexOfChild(StyleWrapper sw, RuleWrapper rw){
                return sw.indexOf(rw);
        }

        private int getIndexOfChild(RuleWrapper rw, ILegendPanel leg){
                return rw.indexOf(leg);
        }

}
