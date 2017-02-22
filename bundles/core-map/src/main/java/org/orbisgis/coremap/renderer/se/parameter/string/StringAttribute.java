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
package org.orbisgis.coremap.renderer.se.parameter.string;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.xml.bind.JAXBElement;


import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.ValueReference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The {@code ValueReference} implementation of {@code StringParameter}. That means that 
 * this class is used to retrieve string values by using a table 
 * {@code DataSet} as specified in {@link ValueReference ValueReference}.</p>
 * <p>Note that the {@code DataSet} is not directly attached to the class,
 * and must be specified each time you call {@code getValue}.
 * @author Alexis Guéganno, Maxence Laurent
 */
public class StringAttribute extends ValueReference implements StringParameter{

    private static final I18n I18N = I18nFactory.getI18n(StringAttribute.class);

    private String[] restriction;

    /**
     * Creates a new {@code StringAttribute}, that will searches its values in a column named
     * filedName.
     * @param fieldName
     */
    public StringAttribute(String fieldName) {
        super(fieldName);
    }

    /**
     * Create a new instance of {@code StringAttribute}, using a {@code JAXBElement} to retrieve
     * all the needed informations.
     * @param expr
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    public StringAttribute(JAXBElement<String> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public String getValue(ResultSet rs, long fid) throws ParameterException{ // TODO implement
        try {
            if(rs.getRow() != fid) {
                rs.absolute((int)fid);
            }
			Object fieldValue = getFieldValue(rs, fid);
			return fieldValue.toString();
        } catch (SQLException e) {
            throw new ParameterException(I18N.tr("Could not fetch feature attribute \"{0}\"", getColumnName()),e);
        }
    }

    @Override
    public String getValue(Map<String, Object> feature) throws ParameterException {
        return getFieldValue(feature).toString();
    }

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list.clone();
    }

}
