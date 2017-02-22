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
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.RecodeType;


import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.Recode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;

/**
 * <code>Recode</code> implementation that maps input values to color values.
 * @author Maxence Laurent, Alexis Guéganno
 */
public class Recode2Color extends Recode<ColorParameter, ColorLiteral> implements ColorParameter {

        /**
         * Creates a new instance of <code>Recode2Color</code>. The default result value
         * will be <code>fallback</code>, and the values that need to be processed
         * will be retrieved using <code>lookupValue</code>
         * @param fallback
         * @param lookupValue 
         */
        public Recode2Color(ColorLiteral fallback, StringParameter lookupValue) {
                super(fallback, lookupValue);
        }

        /**
         * Creates a new instance of <code>Recode2Color</code>. All the needed objects
         * will be created using the JAXB element given in parameter. Particularly,
         * the <code>MapItem</code>s used in the current recode will be retrieved 
         * from this XML representation.
         * @param expr
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
         */
        public Recode2Color(JAXBElement<RecodeType> expr) throws InvalidStyle {
                RecodeType t = expr.getValue();

                this.setFallbackValue(new ColorLiteral(t.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

                for (MapItemType mi : t.getMapItem()) {
                        this.addMapItem(mi.getKey(),
                                SeParameterFactory.createColorParameter(mi.getValue()));
                }
        }

        @Override
        public Color getColor(ResultSet rs, long fid) throws ParameterException {
                //If we can't retrieve any information in sds, getParameter will provide a 
                //default value, so we won't obtain any error.
                return getParameter(rs, fid).getColor(rs, fid);
        }

        @Override
        public Color getColor(Map<String,Object> map) throws ParameterException {
                return getParameter(map).getColor(map);
        }
}
