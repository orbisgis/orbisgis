/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter.string;

import java.awt.Color;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.RecodeType;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * <code>Recode</code> implementation that maps input values to {@code String} values.
 * @author maxence, alexis
 */
public final class Recode2String extends Recode<StringParameter, StringLiteral> implements StringParameter {

        private String[] restriction = new String[]{};
        private final static Logger LOGGER = Logger.getLogger(Recode2String.class);
        private final static I18n I18N = I18nFactory.getI18n(Recode2String.class);
        
        /**
         * Creates a new instance of <code>Recode2String</code>. The default result value
         * will be <code>fallback</code>, and the values that need to be processed
         * will be retrieved using <code>lookupValue</code>
         * @param fallback
         * @param lookupValue 
         */
        public Recode2String(StringLiteral fallback, StringParameter lookupValue) {
                super(fallback, lookupValue);
        }

        /**
         * Creates a new instance of <code>Recode2String</code>. All the needed objects
         * will be created using the JAXB element given in parameter. Particularly,
         * the <code>MapItem</code>s used in the current recode will be retrieved 
         * from this XML representation.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Recode2String(JAXBElement<RecodeType> expr) throws InvalidStyle {
                RecodeType t = expr.getValue();

                this.setFallbackValue(new StringLiteral(t.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

                for (MapItemType mi : t.getMapItem()) {
                        this.addMapItem(mi.getKey(),
                                SeParameterFactory.createStringParameter(mi.getValue()));
                }
        }

        @Override
        public String getValue(DataSource sds, long fid) {
                try {
                        return getParameter(sds, fid).getValue(sds, fid);
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Fallback"), ex);
                        return getFallbackValue().getValue(sds, fid);
                }
        }

        @Override
        public String getValue(Map<String,Value> map) {
                try {
                        return getParameter(map).getValue(map);
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Fallback"), ex);
                        return getFallbackValue().getValue(map);
                }
        }

        @Override
        public int addMapItem(String key, StringParameter value) {
                value.setRestrictionTo(restriction);
                return super.addMapItem(key, value);
        }

        @Override
        public void setRestrictionTo(String[] list) {
                restriction = list.clone();
                for (int i = 0; i < this.getNumMapItem(); i++) {
                        getMapItemValue(i).setRestrictionTo(list);
                }
        }
}
