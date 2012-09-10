/*
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
package org.orbisgis.view.map.mapsManager;

import java.util.List;
import javax.swing.tree.MutableTreeNode;
import org.orbisgis.core.common.BeanPropertyChangeSupport;

/**
 * This is the base class for all items in the tree.
 * It gives the ability to listen for item change
 * @author Nicolas Fortin
 */
public abstract class AbstractTreeNode extends BeanPropertyChangeSupport implements MutableTreeNode {
        private static final long serialVersionUID = 1L;
        // Properties        
        private String label = "none";
        public static final String PROP_LABEL = "label";
        public static final String PROP_CHILDREN = "children";

        @Override
        public String toString() {
                return label;
        }

        /**
         * Get the value of label of the TreeNode
         *
         * @return the value of label
         */
        public String getLabel() {
                return label;
        }

        /**
         * Set the value of label of the TreeNode
         *
         * @param label new value of label
         */
        public void setLabel(String label) {
                String oldLabel = this.label;
                this.label = label;
                propertyChangeSupport.firePropertyChange(PROP_LABEL, oldLabel, label);
        }
        
        /**
         * Fire the insertion of a new children
         * @param children
         * @param index 
         */
        protected void fireInsertChildren(AbstractTreeNode children, int index) {
                propertyChangeSupport.fireIndexedPropertyChange(PROP_CHILDREN, index, null, children);
        }
        
        /**
         * 
         * @return The children list or Null if getAllowsChildren is false
         */
        abstract List<AbstractTreeNode> getChildren();
        
        /**
         * Children list have been updated.
         * @param old_Children Children list before the update
         */
        protected void fireChildrensChange(List<AbstractTreeNode> old_Children) {
                propertyChangeSupport.firePropertyChange(PROP_CHILDREN, old_Children, getChildren());
        }
}
