/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

public abstract class AbstractConversionRule implements ConversionRule {

        @Override
        public String getSQL(String fieldName, Type fieldType) {
                return fieldName + " " + getTypeName()
                        + getGlobalConstraintExpr(fieldType);
        }

        protected String getGlobalConstraintExpr(Type fieldType) {
                StringBuilder ret = new StringBuilder("");
                boolean notNull = fieldType.getBooleanConstraint(Constraint.NOT_NULL);
                if (notNull) {
                        ret.append(" NOT NULL ");
                }

                boolean unique = fieldType.getBooleanConstraint(Constraint.UNIQUE);
                if (unique) {
                        ret.append(" UNIQUE ");
                }

                return ret.toString();
        }

        @Override
        public int[] getValidConstraints() {
                return addGlobalConstraints(new int[0]);
        }

        public int[] addGlobalConstraints(int... constraints) {
                int[] ret = new int[constraints.length + 4];
                System.arraycopy(constraints, 0, ret, 0, constraints.length);
                ret[constraints.length] = Constraint.NOT_NULL;
                ret[constraints.length + 1] = Constraint.PK;
                ret[constraints.length + 2] = Constraint.READONLY;
                ret[constraints.length + 3] = Constraint.UNIQUE;

                return ret;
        }

        @Override
        public Type createType() {
                return createType(new Constraint[0]);
        }

        protected abstract int getOutputTypeCode();

        @Override
        public Type createType(Constraint[] constraints) {
                int[] allowed = getValidConstraints();
                for (Constraint constraint : constraints) {
                        if (!contains(allowed, constraint.getConstraintCode())) {
                                throw new InvalidTypeException("Cannot use "
                                        + constraint.getConstraintCode() + " in "
                                        + getTypeName() + " type");
                        }
                }
                return TypeFactory.createType(getOutputTypeCode(), getTypeName(),
                        constraints);
        }

        private boolean contains(int[] allowed, int constraintCode) {
                for (int object : allowed) {
                        if (object == constraintCode) {
                                return true;
                        }
                }

                return false;
        }
}
