/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process.model;

import java.net.URI;

/**
 * Value of a LiteralData
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#27
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralValue {
    /** Value */
    private Number value;
    /** DataType of the value */
    private DataType dataType;
    /** URI to the unit of the value */
    private URI uom;

    /**
     * Constructor giving the value represented.
     * @param value Value represented. Can not be null.
     * @throws IllegalArgumentException Exception get on setting a null value.
     */
    public LiteralValue(Number value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        this.value = value;
        this.dataType = null;
        this.uom = null;
    }

    /**
     * Sets the value represented.
     * @param value The new value represented. Can not be null.
     * @throws IllegalArgumentException Exception get on setting a null value.
     */
    public void setValue(Number value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        this.value = value;
    }

    /**
     * Returns the value.
     * @return The value.
     */
    public Number getValue() {
        return value;
    }

    /**
     * Sets the dataType of the value.
     * @param dataType DataType of the value.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns the DataType of the value.
     * @return The DataType of the value.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the unit of the value.
     * @param uom The unit or the value.
     */
    public void setUom(URI uom) {
        this.uom = uom;
    }

    /**
     * Returns the unit of the value.
     * @return The unit of the value.
     */
    public URI getUom() {
        return uom;
    }
}
