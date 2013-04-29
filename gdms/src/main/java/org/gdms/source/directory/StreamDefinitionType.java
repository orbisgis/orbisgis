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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.stream.StreamSourceDefinition;

import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 *
 * @author Vincent Dépériers
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stream-definition-type")
public class StreamDefinitionType extends DefinitionType {


        @XmlAttribute(name = "src")
        @XmlSchemaType(name = "anyURI")
        protected String src;

        /**
         * Gets the value of the src property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getSrc() {
            return src;
        }

        /**
         * Sets the value of the src property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setSrc(String value) {
            this.src = value;
        }

        @Override
        public DataSourceDefinition toDataSourceDefinition() throws InstantiationException,
                IllegalAccessException, ClassNotFoundException {
                try {
                    return StreamSourceDefinition.createFromURI(URI.create(src));
                } catch (UnsupportedEncodingException ex) {
                    throw new InstantiationException("Could not convert Uri to DataSource "+src);
                }
        }
}
