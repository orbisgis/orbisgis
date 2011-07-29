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



package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ValueReferenceType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * Used to dinamycally retrieve color values from a table.
 * @author alexis
 */
public class ColorAttribute extends ValueReference implements ColorParameter {

    /**
     * Instanciates the attribute with the name of the column where to search.
     * @param fieldName 
     */
    public ColorAttribute(String fieldName) {
        super(fieldName);
    }

    /**
     * Build a <code>colorAttribute</code> from its JAXB representation.
     * @param expr
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public ColorAttribute(JAXBElement<ValueReferenceType> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public Color getColor(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(sds, fid).getAsString());
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"",e);
        }
    }
}
