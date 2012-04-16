/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeModel;
import org.orbisgis.core.ui.editorViews.toc.wrapper.RuleWrapper;
import org.orbisgis.core.ui.editorViews.toc.wrapper.StyleWrapper;
import org.orbisgis.legend.Legend;

/**
 * This tree model is used in the legend edition panel. Its depth is only 2 :
 * <ul>
 * <li>Level 0 (root) : the Style we are editing.</li>
 * <li>Level 1 : the Rules</li>
 * <li>Level 2 : the Legends</li>
 * </ul>
 * @author alexis
 */
public class LegendTreeModel extends  AbstractTreeModel {

        private StyleWrapper root;

        public LegendTreeModel(JTree tree, StyleWrapper r){
                super(tree);
                root = r;
        }

        public void refresh(){
                //This is way too violent : it would be faster to create a
                //dedicated event for each change !
                fireEvent(new TreePath(root));
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
                return node instanceof Legend;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
                if(parent instanceof StyleWrapper && child instanceof RuleWrapper){
                        return getIndexOfChild((StyleWrapper) parent, (RuleWrapper) child);
                } else if(parent instanceof RuleWrapper && child instanceof Legend){
                        return getIndexOfChild((RuleWrapper) parent, (Legend) child);
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
                if(parent instanceof RuleWrapper && newElt instanceof Legend){
                        if(current instanceof Legend){
                                addElement((RuleWrapper) parent, (Legend) newElt, (Legend) current);
                        } else {
                                addElement((RuleWrapper) parent, (Legend) newElt, null);
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
                if(parent instanceof RuleWrapper && oldElt instanceof Legend){
                        removeElement((RuleWrapper) parent, (Legend) oldElt);
                } else if(parent instanceof StyleWrapper && oldElt instanceof RuleWrapper){
                        removeElement((RuleWrapper) oldElt);
                }
        }

        private void removeElement(RuleWrapper rw, Legend old){
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
                if(parent instanceof RuleWrapper && elt instanceof Legend){
                        moveElementDown((RuleWrapper) parent, (Legend) elt);
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
                if(parent instanceof RuleWrapper && elt instanceof Legend){
                        moveElementUp((RuleWrapper) parent, (Legend) elt);
                } else if(parent instanceof StyleWrapper && elt instanceof RuleWrapper){
                        moveElementUp((RuleWrapper) elt);
                }
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

        private void addElement(RuleWrapper parent, Legend newElt, Legend current){
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
        private void moveElementDown(RuleWrapper rw, Legend l){
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

        private void moveElementUp(RuleWrapper rw, Legend l){
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

        private Legend getChild(RuleWrapper r, int i){
                if(i>=0 && i<r.getSize()){
                        return r.getLegend(i);
                }
                return null;
        }

        private int getIndexOfChild(StyleWrapper sw, RuleWrapper rw){
                return sw.indexOf(rw);
        }

        private int getIndexOfChild(RuleWrapper rw, Legend leg){
                return rw.indexOf(leg);
        }

}
