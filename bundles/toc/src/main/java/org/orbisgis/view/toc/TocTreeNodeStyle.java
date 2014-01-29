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
package org.orbisgis.toc;

import java.util.Enumeration;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.orbisgis.core.renderer.se.Style;

/**
 * The decorator for a tree node style
 */
public class TocTreeNodeStyle implements MutableTreeNode  {
        private Style style;

        public TocTreeNodeStyle(Style style) {
                this.style = style;
        }

        public Style getStyle() {
                return style;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof TocTreeNodeStyle) {
                        final TocTreeNodeStyle other = (TocTreeNodeStyle) obj;
                        return this.style != null && this.style.equals(other.style);
                } else {
                        return false;
                }
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
        public Enumeration<TreeNode> children() {
                return null;
        }

        @Override
        public void insert(MutableTreeNode mtn, int i) {
        }

        @Override
        public void remove(int i) {
        }

        @Override
        public void remove(MutableTreeNode mtn) {
        }

        @Override
        public void setUserObject(Object o) {
                //User edit the style name
                style.setName(o.toString());
        }

        @Override
        public void removeFromParent() {
        }

        @Override
        public void setParent(MutableTreeNode mtn) {
        }
        
}
