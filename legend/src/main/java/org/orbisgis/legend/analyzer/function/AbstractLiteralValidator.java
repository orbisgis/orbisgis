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
package org.orbisgis.legend.analyzer.function;

import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.legend.AbstractAnalyzer;

/**
 * Specializations of this class will be both instances of {@link
 * CategorizeValidator} and {@link RecodeValidator}. A {@code Categorize}
 * instance will be valid if its classes and threshold values are {@code Literal}
 * instances. A {@code Recode} instance will be valid if its item values are
 * {@code Literal} instances.
 * @author Alexis Guéganno
 */
public abstract class AbstractLiteralValidator extends AbstractAnalyzer implements
                CategorizeValidator, RecodeValidator {

        /**
         * Validate a {@code Recode} instance. It will be valid if and only if
         * its map values are all {@code Literal} instances.
         * @param rc
         * @return
         */
        @Override
        public boolean validateRecode(Recode rc) {
                for(int i = 0; i < rc.getNumMapItem(); i++){
                        if(!(rc.getMapItemValue(i) instanceof Literal)){
                                return false;
                        }
                }
                return rc.getLookupValue() instanceof StringAttribute;
        }

        /**
         * Validate a {@code Categorize} instance. It will be valid if and only
         * if its class and threshold values are all {@code Literal} instances.
         * @param cg
         * @return
         */
        @Override
        public boolean validateCategorize(Categorize cg){
                if(!(cg.getClassValue(0) instanceof Literal)){
                        return false;
                }
                for(int i=1; i<cg.getNumClasses();i++){
                        if (!((cg.getClassValue(i) instanceof Literal)
                                && (cg.getClassThreshold(i-1) instanceof Literal))) {
                                return false;
                        }
                }
                return cg.getLookupValue() instanceof RealAttribute;
        }

}
