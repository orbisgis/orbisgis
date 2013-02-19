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
package org.orbisgis.core.renderer.se.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * Search for the names of the features that are used in the visited tree of
 * {@link SymbolizerNode} instances.
 * @author Alexis Guéganno
 */
public class FeaturesVisitor implements ISymbolizerVisitor {

        private Set<String> res = new HashSet<String>();

        /**
         * Recursively visits {@code sn} and all its children, searching for
         * feature-dependant nodes.
         * @param sn
         */
        @Override
        public void visitSymbolizerNode(SymbolizerNode sn) {
                if(!res.isEmpty()){
                        res = new HashSet<String>();
                }
                visitImpl(sn);
        }

        /**
         * The method that does the work... It is not callable directly by the
         * clients, as it does not clean the inner HashSet. If you want
         * to use it directly, inherit this class.
         * @param sn
         */
        protected void visitImpl(SymbolizerNode sn){
                List<SymbolizerNode> children = sn.getChildren();
                if(sn instanceof ValueReference){
                        res.add(((ValueReference)sn).getColumnName());
                }
                for(SymbolizerNode c : children){
                        visitImpl(c);
                }

        }

        /**
         * Gets the {@code HashSet<String>} instance that contains all the field
         * names needed to use safely the last visited {@code SymbolizerNode}.
         * @return
         */
        public Set<String> getResult(){
                return res;
        }

}
