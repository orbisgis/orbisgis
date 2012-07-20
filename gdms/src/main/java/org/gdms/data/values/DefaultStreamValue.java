/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import com.vividsolutions.jts.geom.Envelope;

import org.gdms.data.stream.GeoStream;
import org.gdms.data.types.Type;

/**
 *
 * @author Vincent Dépériers
 */
public class DefaultStreamValue extends AbstractValue implements StreamValue {

        private GeoStream m_GeoStream;
        
        /**
         * Creat a new DefaultStreamValue
         * @param geoStream 
         */
        public DefaultStreamValue(GeoStream geoStream) {
                this.m_GeoStream = geoStream;
        }
        
        /**
         * compare to an Value-objet 
         * @param obj
         * @return 
         */
        @Override
        public BooleanValue equals(Value obj) {
                if (obj instanceof StreamValue) {
                        return ValueFactory.createValue(m_GeoStream.equals(((StreamValue) obj).getAsStream()));
                } else {
                        return ValueFactory.createValue(false);
                }
        }

        /**
         * Get the hashCode
         * @return 
         */
        @Override
        public int hashCode() {
                return this.m_GeoStream.hashCode();
        }

        /**
         * Get the String Value, "Envelope"
         * @param writer
         * @return 
         */
        @Override
        public String getStringValue(ValueWriter writer) {
                return "Envelope";
        }

        /**
         * Get the type of the value-Stream
         * @return 
         */
        @Override
        public int getType() {
                return Type.STREAM;
        }

        /**
         * Get the bytes
         * @return 
         */
        @Override
        public byte[] getBytes() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * Set the value with a parameter
         * @param value 
         */
        @Override
        public void setValue(GeoStream value) {
                this.m_GeoStream = value;
        }
        
        /**
         * Get the GeoStream
         * @return 
         */
        @Override
        public GeoStream getAsStream() {
                return this.m_GeoStream;
        }      
}
