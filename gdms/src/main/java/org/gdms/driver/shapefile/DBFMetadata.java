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
/**
 *
 */
package org.gdms.driver.shapefile;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

class DBFMetadata implements Metadata {

        private List<Integer> mapping;
        private Metadata metadata;

        DBFMetadata(Metadata metadata) throws DriverException {
                this.metadata = metadata;

                mapping = new ArrayList<Integer>();
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (!TypeFactory.isVectorial(metadata.getFieldType(i).getTypeCode())) {
                                mapping.add(i);
                        }
                }
                if (mapping.size() + 1 < metadata.getFieldCount()) {
                        throw new IllegalArgumentException("The data source "
                                + "has more than one spatial field");
                } else if (mapping.size() + 1 > metadata.getFieldCount()) {
                        throw new IllegalArgumentException("The data source "
                                + "has no spatial field");
                }
        }

        @Override
        public int getFieldCount() throws DriverException {
                return metadata.getFieldCount() - 1;
        }

        @Override
        public String getFieldName(int fieldId) throws DriverException {
                return metadata.getFieldName(mapping.get(fieldId));
        }

        @Override
        public Type getFieldType(int fieldId) throws DriverException {
                return metadata.getFieldType(mapping.get(fieldId));
        }

        public List<Integer> getMapping() {
                return mapping;
        }

        @Override
        public int getFieldIndex(String fieldName) throws DriverException {
                return metadata.getFieldIndex(fieldName);
        }

        @Override
        public Schema getSchema() {
                return null;
        }

        @Override
        public String[] getFieldNames() throws DriverException {
                String[] s = new String[mapping.size()];
                for (int i = 0; i < mapping.size(); i++) {
                        s[i] = metadata.getFieldName(mapping.get(i));
                }
                return s;
        }
}
