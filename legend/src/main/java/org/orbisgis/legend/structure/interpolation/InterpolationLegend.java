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

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;

/**
 * The default representation of an interpolation, in the legend. If obtained
 * during an analysis, it is supposed to mean that none of the other, more
 * accurate cases, has been recognized in the input {@code Interpolate2Real}
 * instance used by the analyzer.
 * @author Alexis Guéganno
 */
public abstract class InterpolationLegend implements NumericLegend, IInterpolationLegend {

        private Interpolate2Real interp;

        /**
         * Build a new {@code InterpolationLegend}
         * @param inter
         */
        public InterpolationLegend(Interpolate2Real inter){
                interp = inter;
        }

        /**
         * Get the {@code Interpolate2Real} instance associated to this
         * {@code InterpolationLegend}.
         * @return
         */
        public Interpolate2Real getInterpolation(){
                return interp;
        }

        @Override
        public SeParameter getParameter() {
                return getInterpolation();
        }

        @Override
        public double getFirstData() {
            return interp.getInterpolationPoint(0).getData();
        }
        
        @Override
        public void setFirstData(double d) {
            interp.getInterpolationPoint(0).setData(d);
        }

        @Override
        public double getSecondData() {
            return interp.getInterpolationPoint(1).getData();
        }

        @Override
        public void setSecondData(double d) {
            interp.getInterpolationPoint(1).setData(d);
        }

        @Override
        public double getFirstValue() throws ParameterException {
            return interp.getInterpolationPoint(0).getValue().getValue(null, 0);
        }

        @Override
        public void setFirstValue(double d) {
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(0).getValue();
            rl.setValue(d);
        }

        @Override
        public double getSecondValue() throws ParameterException {
            return interp.getInterpolationPoint(1).getValue().getValue(null, 0);
        }

        @Override
        public void setSecondValue(double d) {
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(1).getValue();
            rl.setValue(d);
        }

}
