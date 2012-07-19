 
package org.orbisgis.view.toc;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.orbisgis.core.renderer.se.Style;

/**
 * The decorator for a tree node style
 */
public class TocTreeNodeStyle implements TreeNode  {
        private Style style;

        public TocTreeNodeStyle(Style style) {
                this.style = style;
        }

        public Style getStyle() {
                return style;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj == null) {
                        return false;
                }
                if (!(obj instanceof TocTreeNodeStyle)) {
                        return false;
                }
                final TocTreeNodeStyle other = (TocTreeNodeStyle) obj;
                
                return this.style != null && this.style.equals(other.style);
        }

        @Override
        public int hashCode() {
                int hash = 5;
                hash = 97 * hash + (this.style != null ? this.style.hashCode() : 0);
                return hash;
        }

        
        
        @Override
        public TreeNode getChildAt(int i) {
                return null;
        }

        @Override
        public int getChildCount() {
                return 0;
        }

        @Override
        public TreeNode getParent() {
                return new TocTreeNodeLayer(style.getLayer());
        }

        @Override
        public int getIndex(TreeNode tn) {
                return -1;
        }

        @Override
        public boolean getAllowsChildren() {
                return false;
        }

        @Override
        public boolean isLeaf() {
                return true;
        }

        @Override
        public Enumeration children() {
                return null;
        }
        
}
