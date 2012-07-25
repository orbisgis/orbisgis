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
package org.gdms.source.directory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.stream.StreamSourceDefinition;

/**
 *
 * @author Vincent Dépériers
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stream-definition-type")
public class StreamDefinitionType extends DefinitionType {

        @XmlAttribute(required = true)
        protected String host;
        @XmlAttribute(required = true)
        protected String port;
        @XmlAttribute(name = "layer-name", required = true)
        protected String layerName;
        @XmlAttribute(required = true)
        protected String type;    
        @XmlAttribute(required = true)
        protected String format;
        @XmlAttribute(required = true)
        protected String srs;
        

        /**
         * Gets the value of the host property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHost() {
                return host;
        }

        /**
         * Sets the value of the host property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHost(String value) {
                this.host = value;
        }

        /**
         * Gets the value of the port property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPort() {
                return port;
        }

        /**
         * Sets the value of the port property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPort(String value) {
                this.port = value;
        }

        /**
         * Gets the value of the tableName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLayerName() {
                return layerName;
        }

        /**
         * Sets the value of the tableName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLayerName(String value) {
                this.layerName = value;
        }

      /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
                return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
                this.type = value;
        }
        
        /**
         * Gets the value of the format property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImageFormat() {
                return format;
        }

        /**
         * Sets the value of the format property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImageFormat(String value) {
                this.format = value;
        }
        
        /**
         * Gets the value of the srs property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSRS() {
                return srs;
        }

        /**
         * Sets the value of the srs property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSRS(String value) {
                this.srs = value;
        }

        @Override
        public DataSourceDefinition toDataSourceDefinition() {
                return StreamSourceDefinition.createFromXML(this);
        }
}
