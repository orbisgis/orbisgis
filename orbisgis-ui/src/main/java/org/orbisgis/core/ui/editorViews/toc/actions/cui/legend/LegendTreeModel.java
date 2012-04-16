/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import javax.swing.JTree;
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
