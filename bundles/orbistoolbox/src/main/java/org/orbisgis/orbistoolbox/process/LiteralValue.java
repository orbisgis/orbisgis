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
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for moredetails.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process;

import java.net.URI;

/**
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#27
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralValue {
    private String
            value;
    private DataType
            dataType;
    private URI
            uom;

    public LiteralValue(String value)
            throws
            IllegalArgumentException {
        if (value ==
                null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        this.value =
                value;
        this.dataType =
                null;
        this.uom =
                null;
    }

    public void setValue(String value)
            throws
            IllegalArgumentException {
        if (value ==
                null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        this.value =
                value;
    }

    public String getRawValue() {
        return value;
    }

    public Object getParsedValue() {
        if (dataType ==
                null) {
            return value;
        }
        switch (dataType) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case DECIMAL:
                return Long.parseLong(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case FLOAT:
                return Float.parseFloat(value);
            case INTEGER:
                return Integer.parseInt(value);
            case STRING:
                return value;
        }
        return value;
    }

    public void setDataType(DataType dataType) {
        this.dataType =
                dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setUom(URI uom) {
        this.uom =
                uom;
    }

    public URI getUom() {
        return uom;
    }
}
