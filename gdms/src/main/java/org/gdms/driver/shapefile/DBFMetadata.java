/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
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
import org.gdms.driver.DriverException;

class DBFMetadata implements Metadata {

        private List<Integer> mapping;
        private Metadata metadata;

        DBFMetadata(Metadata metadata) throws DriverException {
                this.metadata = metadata;

                mapping = new ArrayList<Integer>();
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (metadata.getFieldType(i).getTypeCode() != Type.GEOMETRY) {
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
