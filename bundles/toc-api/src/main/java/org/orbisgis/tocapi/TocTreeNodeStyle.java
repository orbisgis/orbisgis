/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.tocapi;

import java.util.Enumeration;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.orbisgis.coremap.renderer.se.Style;

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
