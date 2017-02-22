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
package org.orbisgis.legend.structure.interpolation;

import org.orbisgis.coremap.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.coremap.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.coremap.renderer.se.parameter.real.RealFunction;
import org.orbisgis.legend.LookupFieldName;

/**
 * Analysis associated to an interpolation made on the square root of a numeric
 * value.</p>
 * <p>Note that we don't care that one of the values given to compute the
 * interpolation is 0 or not. We do it, that's all.
 * @author Alexis Guéganno
 */
public class SqrtInterpolationLegend extends InterpolationLegend {

        /**
         * Build a new Legend using the given {@code Interpolate2Real} instance.
         * @param inter
         */
        public SqrtInterpolationLegend(Interpolate2Real inter){
                super(inter);
        }

        @Override
        public String getLookupFieldName(){
                return getRealAttribute().getColumnName();
        }

        @Override
        public void setLookupFieldName(String name){
                getRealAttribute().setColumnName(name);
        }

        private RealAttribute getRealAttribute(){
                Interpolate2Real interp = getInterpolation();
                //We have a RealAttribute in a SQRT RealFunction
                RealFunction rf = (RealFunction)interp.getLookupValue();
                return (RealAttribute) (rf).getOperand(0);
        }

}
