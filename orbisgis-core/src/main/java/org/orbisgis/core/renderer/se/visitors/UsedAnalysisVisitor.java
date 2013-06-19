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

import java.util.List;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * This visitor will search for all the analysis that are made in a tree of
 * {@code SymbolizerNode}. It searches recursively for instances of {@code
 * SeParamater} that are instances of {@code Recode}, {@code Categorize} or
 * {@code Interpolate}.
 * @author Alexis Guéganno
 */
public class UsedAnalysisVisitor implements ISymbolizerVisitor {

        private UsedAnalysis usedAnalysis;

        @Override
        public void visitSymbolizerNode(SymbolizerNode sn) {
                usedAnalysis = new UsedAnalysis();
                visitImpl(sn);
        }

        /**
         * The method that does the work... It is not callable directly by the
         * clients, as it does not clean the inner UsedAnalysis. If you want
         * to use it directly, inherit this class.
         * @param sn
         */
        protected final void visitImpl(SymbolizerNode sn) {
                if(sn instanceof SeParameter){
                        usedAnalysis.include((SeParameter) sn);
                }
                List<SymbolizerNode> lsn = sn.getChildren();
                for(SymbolizerNode sym : lsn){
                        visitImpl(sym);
                }
        }

        /**
         * Gets the analysis that have been found in the last visited tree of
         * {@code SymbolizerNode}.
         * @return
         */
        public UsedAnalysis getUsedAnalysis() {
                return usedAnalysis;
        }

}
