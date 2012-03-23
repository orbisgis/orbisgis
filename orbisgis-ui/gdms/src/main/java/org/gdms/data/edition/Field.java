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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
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
 **/
package org.gdms.data.edition;

import org.gdms.data.types.Type;

/**
 * A field contains a name and {@link Type}, and is used to define a column in a table.
 * It is located in the table by an index.
 */
public class Field {

        private int originalIndex;
        private String name;
        private Type type;

        /**
         * public constructor
         * @param originalIndex
         * @param name
         * @param type
         */
        public Field(int originalIndex, String name, Type type) {
                this.originalIndex = originalIndex;
                this.name = name;
                this.type = type;
        }

        /**
         *
         * @return the name of the field
         */
        public String getName() {
                return name;
        }

        /**
         * Set the name of the field
         * @param name : the new namme
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         *
         * @return the index
         */
        public int getOriginalIndex() {
                return originalIndex;
        }

        /**
         *
         * @param originalIndex the new index
         */
        public void setOriginalIndex(int originalIndex) {
                this.originalIndex = originalIndex;
        }

        /**
         *
         * @return the {@link org.gdms.data.types.Type} of the field.
         */
        public Type getType() {
                return type;
        }

        /**
         *
         * @param type the new {@link org.gdms.data.types.Type} of the field
         */
        public void setType(Type type) {
                this.type = type;
        }
}