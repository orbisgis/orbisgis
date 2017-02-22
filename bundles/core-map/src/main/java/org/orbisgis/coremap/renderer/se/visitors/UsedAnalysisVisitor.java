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
package org.orbisgis.coremap.renderer.se.visitors;

import java.util.List;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;

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
