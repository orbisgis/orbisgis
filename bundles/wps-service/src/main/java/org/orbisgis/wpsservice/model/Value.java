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

package org.orbisgis.wpsservice.model;

/**
 * Simple value. The value is represented as a String.
 * This class comes from the OWS specification : ows:ValueType and ows:Value
 *
 * @author Sylvain PALOMINOS
 */

@Deprecated
public class Value<T> extends Values {
    /** String representation of the value. */
    private T value;

    /**
     * Main constructor in which the value must be passed as a String.
     * @param value Not null value.
     */
    public Value(T value) {
        this.value = value;
    }

    /**
     * Returns the value.
     * @return The value.
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the the value. The value given as parameter can not be null.
     * @param value Not null value.
     */
    public void setValue(T value) {
        this.value = value;
    }
}
