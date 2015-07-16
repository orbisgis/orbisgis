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

package org.orbisgis.orbistoolbox.model;

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
    private Values data;
    /** DataType of the data */
    private DataType dataType;
    /** URI to the unit of the data */
    private URI uom;

    /**
     * Constructor giving the data represented.
     * @param data Data represented. Can not be null.
     * @throws MalformedScriptException Exception get on setting a null data.
     */
    public LiteralValue(Values data) throws MalformedScriptException {
        if (data == null) {
            throw new MalformedScriptException(this.getClass(), "data", "can not be null");
        }
        this.data = data;
        this.dataType = null;
        this.uom = null;
    }

    /**
     * Constructor with no data, so the default data will be used.
     */
    public LiteralValue() {
        this.data = null;
        this.dataType = null;
        this.uom = null;
    }

    /**
     * Sets the data represented.
     * @param data The new data represented. Can not be null.
     * @throws MalformedScriptException Exception get on setting a null data.
     */
    public void setData(Values data) throws MalformedScriptException {
        if (data == null) {
            throw new MalformedScriptException(this.getClass(), "data", "can not be null");
        }
        this.data = data;
    }

    /**
     * Returns the data.
     * @return The data.
     */
    public Values getData() {
        return data;
    }

    /**
     * Sets the dataType of the data.
     * @param dataType DataType of the data.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns the DataType of the data.
     * @return The DataType of the data.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the unit of the data.
     * @param uom The unit or the data.
     */
    public void setUom(URI uom) {
        this.uom = uom;
    }

    /**
     * Returns the unit of the data.
     * @return The unit of the data.
     */
    public URI getUom() {
        return uom;
    }
}
