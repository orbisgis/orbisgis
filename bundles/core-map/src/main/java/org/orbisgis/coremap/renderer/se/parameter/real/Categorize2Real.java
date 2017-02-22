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
package org.orbisgis.coremap.renderer.se.parameter.real;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import net.opengis.fes._2.LiteralType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;


import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;

/**
 * A categorization from {@code RealParameter} to {@code RealParamter}
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class Categorize2Real extends Categorize<RealParameter, RealLiteral> implements RealParameter {

        private RealParameterContext ctx;

        /**
         * Build a new {@code Categorize2Real} with the given parameters. Built using 
         * {@link Categorize#Categorize(org.orbisgis.coremap.renderer.se.parameter.SeParameter,
         * org.orbisgis.coremap.renderer.se.parameter.SeParameter,
         * org.orbisgis.coremap.renderer.se.parameter.real.RealParameter) Categorize}
         * @param initialClass
         * The value of the first class.
         * @param fallback
         * The default value if an input can't be processed.
         * @param lookupValue 
         * The {@code RealParameter} used to retrieve the input values.
         */
        public Categorize2Real(RealParameter initialClass, RealLiteral fallback, RealParameter lookupValue) {
                super(initialClass, fallback, lookupValue);
                this.setContext(ctx);
        }

        /**
         * Build a new {@code Categorize2Real} from a JAXB element.
         * @param expr
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
         */
        public Categorize2Real(CategorizeType expr) throws InvalidStyle {
                
                this.setFallbackValue(new RealLiteral(expr.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createRealParameter(expr.getLookupValue()));


                Iterator<Object> it = expr.getThresholdAndValue().iterator();
                this.put(new RealLiteral(Double.NEGATIVE_INFINITY), SeParameterFactory.createRealParameter((ParameterValueType)it.next()));
                // Fetch class values and thresholds
                while (it.hasNext()) {
                        RealLiteral th = new RealLiteral((LiteralType)(it.next()));
                        RealParameter vl = SeParameterFactory.createRealParameter((ParameterValueType)it.next());
                        this.put(th, vl);
                }

                if (expr.getThresholdBelongsTo() == ThresholdBelongsToType.PRECEDING) {
                        this.setThresholdsPreceding();
                }
                else {
                        this.setThresholdsSucceeding();
                }
             
                super.setPropertyFromJaxB(expr);
        }

        @Override
        public Double getValue(ResultSet rs, long fid) throws ParameterException{
            if (rs == null){
                throw new ParameterException("No feature");
            }
            return getParameter(rs, fid).getValue(rs, fid);
        }

        @Override
        public Double getValue(Map<String,Object> map) throws ParameterException{
            if (map == null){
                throw new ParameterException("No feature");
            }
            return getParameter(map).getValue(map);
        }

	@Override
	public void setValue(int i, RealParameter value){
		super.setValue(i, value);
		if (value != null){
			value.setContext(ctx);
		}
	}

	@Override
	public void setFallbackValue(RealLiteral l){
		super.setFallbackValue(l);
		if (l != null){
			l.setContext(ctx);
		}
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;
		this.getFallbackValue().setContext(ctx);

		for (int i=0; i<this.getNumClasses();i++){
			RealParameter classValue = this.get(i);
			classValue.setContext(ctx);
		}

	}

	@Override
	public String toString(){
		return "NA";
	}

	@Override
	public RealParameterContext getContext() {
		return ctx;
	}

        /**
         * Always return 0, whatever the object is...
         * @param o
         * @return 
         */
        @Override
        public int compareTo(Object o) {
                return 0;
        }

}
