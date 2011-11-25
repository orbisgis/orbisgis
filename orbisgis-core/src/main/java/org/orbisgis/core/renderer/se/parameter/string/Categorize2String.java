/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.renderer.se.parameter.string;

import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
/**
 * A categorization from {@code RealParameter} to {@code StringParamter}
 * @author alexis, maxence
 */
public final class Categorize2String extends Categorize<StringParameter, StringLiteral> implements StringParameter {

    private String[] restriction;

        /**
         * Build a new {@code Categorize2String} with the given parameters. Built using 
         * {@link Categorize#Categorize(org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.real.RealParameter) Categorize}
         * @param initialClass
         * The value of the first class.
         * @param fallback
         * The default value if an input can't be processed.
         * @param lookupValue 
         * The {@code RealParameter} used to retrieve the input values.
         */
    public Categorize2String(StringParameter initialClass, StringLiteral fallback, RealParameter lookupValue) {
        super(initialClass, fallback, lookupValue);
    }

        /**
         * Build a new {@code Categorize2String} from a JAXB element.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
    public Categorize2String(JAXBElement<CategorizeType> expr) throws InvalidStyle {
        CategorizeType t = expr.getValue();

        this.setFallbackValue(new StringLiteral(t.getFallbackValue()));
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));


        Iterator<JAXBElement<ParameterValueType>> it = t.getThresholdAndValue().iterator();

        this.setClassValue(0, SeParameterFactory.createStringParameter(it.next().getValue()));

        // Fetch class values and thresholds
        while (it.hasNext()) {
            this.addClass(SeParameterFactory.createRealParameter(it.next().getValue()),
                    SeParameterFactory.createStringParameter(it.next().getValue()));
        }

        if (t.getThresholdBelongsTo() == ThresholdBelongsToType.PRECEDING) {
            this.setThresholdsPreceding();
        } else {
            this.setThresholdsSucceeding();
        }
        super.setPropertyFromJaxB(t);
    }

    @Override
    public String getValue(DataSource sds, long fid) {
        try {
            return getParameter(sds, fid).getValue(sds, fid);
        } catch (ParameterException ex) {
            return this.getFallbackValue().getValue( sds, fid);
        }
    }

    @Override
    public void addClass(RealParameter th, StringParameter value){
        super.addClass(th, value);
        value.setRestrictionTo(restriction);
    }

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list.clone();
        for (int i=0;i<this.getNumClasses();i++){
            getClassValue(i).setRestrictionTo(list);
        }
    }
}
