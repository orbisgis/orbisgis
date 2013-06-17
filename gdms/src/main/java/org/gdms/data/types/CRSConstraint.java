/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
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
 * or contact directly: info@orbisgis.org
 */
package org.gdms.data.types;

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.prj.PrjWriter;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;

/**
 * Indicates that the field is part of the primary key.
 *
 */
public final class CRSConstraint extends AbstractConstraint {

    private CoordinateReferenceSystem coordinateReferenceSystem;

    /**
     * Build a CRS constraint using a {@code CoordinateReferenceSystem}
     *
     * @param constraintValue
     */
    public CRSConstraint(CoordinateReferenceSystem constraintValue) {
        coordinateReferenceSystem = constraintValue;
    }

    /**
     * Build a CRS constraint base on the bynary representtion of a
     * {@code CoordinateReferenceSystem}
     *
     * @param constraintValue
     */
    public CRSConstraint(byte[] constraintBytes) {
        coordinateReferenceSystem = DataSourceFactory.getCRSFactory().createFromPrj(new String(constraintBytes));
    }

    /**
     * For use only as a sample in ConstraintFactory.
     */
    public CRSConstraint() {
    }

    @Override
    public int getConstraintCode() {
        return Constraint.CRS;
    }

    @Override
    public boolean allowsFieldRemoval() {
        return true;
    }

    @Override
    public String check(Value value) {
        return null;
    }

    @Override
    public String getConstraintValue() {
        return coordinateReferenceSystem.toString();
    }

    @Override
    public byte[] getBytes() {
        return PrjWriter.crsToWKT(coordinateReferenceSystem).getBytes();
    }

    @Override
    public int getType() {
        return Constraint.CONSTRAINT_TYPE_CRS;
    }

    /**
     * Return the {@code CoordinateReferenceSystem} used by this contraint
     * @return 
     */
    public CoordinateReferenceSystem getCRS() {
        return coordinateReferenceSystem;
    }
}