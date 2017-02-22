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
package org.orbisgis.coremap.renderer.se.parameter.color;

import java.awt.Color;
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
 * The {@code ValueReference} implementation of {@code ColorParameter}. That means that 
 * this class is used to retrieve color values by using a table 
 * {@code DataSet} as specified in {@link ValueReference ValueReference}.</p>
 * <p>Note that the {@code DataSet} is not directly attached to the class,
 * and must be specified each time you call {@code getValue}.
 * @author Maxence Laurent
 * @author Alexis Guéganno
 * @author Erwan Bocher
 */
public class ColorAttribute extends ValueReference implements ColorParameter {

     private static final I18n I18N = I18nFactory.getI18n(ColorAttribute.class);
     
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
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    public ColorAttribute(JAXBElement<String> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public Color getColor(ResultSet rs, long fid) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(rs, fid).toString());
        } catch (SQLException e) {
            
            throw new ParameterException(I18N.tr("Could not fetch feature attribute \"{0}\"", getColumnName()),e);
        }
    }

    @Override
    public Color getColor(Map<String,Object> map) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(map).toString());
        } catch (ParameterException e) {
            throw new ParameterException(I18N.tr("Could not fetch feature attribute \"{0}\"", getColumnName()),e);
        }
    }
}
