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

package org.orbisgis.orbistoolbox.process;

import java.util.List;

/**
 * RawData extends the ComplexData class.
 * It can content any Object as data and the data class is stored as descriptive element.
 *
 * @author Sylvain PALOMINOS
 */

public class RawData extends ComplexData {

    /** Data object. */
    private Object data;

    /**
     * Constructor giving the default format.
     * The Format can not be null and should be set as the default one.
     * @param format Not null default format.
     * @throws IllegalArgumentException Exception get on setting a format which is null or is not the default one.
     */
    public RawData(Format format) {
        super(format);
        data = null;
    }

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null default format.
     * @throws IllegalArgumentException Exception get on setting a format which is null or is not the default one.
     */
    public RawData(List<Format> formatList) {
        super(formatList);
        data = null;
    }

    /**
     * Sets the data contained and store its class as a descriptive element.
     * @param data Data to store.
     */
    public void setData(Object data) {
        this.data = data;
        this.setAnys(null);
        this.addAny(data.getClass().getCanonicalName());
    }

    /**
     * Returns the data.
     * @return The data.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns the data class.
     * @return The data class.
     */
    public Class getDataClass() {
        if (data == null) {
            return null;
        }
        return (Class) getAnys().get(0);
    }
}
