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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.sql.function.table;

import org.gdms.sql.function.Argument;
import org.gdms.sql.function.FunctionSignature;

/**
 * Signature of a Table function.
 * @author Antoine Gourlay
 */
public class TableFunctionSignature implements FunctionSignature {

        private TableDefinition returnTableDef;
        private Argument[] args;

        /**
         * Creates a signature with the specified return table definition and the specified arguments.
         * @param returnTableDef a table definition
         * @param args some arguments. Can be null.
         */
        public TableFunctionSignature(TableDefinition returnTableDef, Argument... args) {
                this.returnTableDef = returnTableDef;
                this.args = args == null ? new Argument[0] : args;
        }

        /**
         * Creates a signature with the specified return table definition and the specified argument.
         * @param returnTableDef a table definition
         * @param arg a non-null argument.
         */
        public TableFunctionSignature(TableDefinition returnTableDef, Argument arg) {
                this.returnTableDef = returnTableDef;
                this.args = new Argument[] { arg };
        }

        @Override
        public boolean isScalarReturn() {
                return false;
        }

        @Override
        public boolean isTableReturn() {
                return true;
        }

        @Override
        public Argument[] getArguments() {
                return args;
        }

        /**
         * @return the table definition
         */
        public TableDefinition getReturnTableDefinition() {
                return returnTableDef;
        }

}
