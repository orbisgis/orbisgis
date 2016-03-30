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

import java.util.ArrayList;
import java.util.List;

/**
 * The ComplexData type does not describe the particular structure for value encoding.
 * Instead, the passed values must comply with the given format and the extended information, if provided.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#22
 *
 * @author Sylvain PALOMINOS
 */

@Deprecated
public abstract class ComplexData extends DataDescription {

    /** Other descriptive elements giving more information about the data format. */
    private List<Object> anys;

    /**
     * Constructor giving the default format.
     * The Format can not be null and should be set as the default one.
     * @param format Not null default format.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    protected ComplexData(Format format) throws MalformedScriptException{
        super(format);
    }

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null default format.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    protected ComplexData(List<Format> formatList) throws MalformedScriptException {
        super(formatList);
    }

    /**
     * Returns the list of the descriptive elements.
     * @return The list of the descriptive elements.
     */
    public final List<Object> getAnys() {
        return anys;
    }

    /**
     * Adds a descriptive elements.
     * @param any A descriptive element.
     */
    protected final void addAny(Object any) {
        if(any != null) {
            if(anys == null){
                anys = new ArrayList<>();
            }
            this.anys.add(any);
        }
    }

    /**
     * Adds a list of descriptive elements.
     * @param anys List of descriptive elements.
     */
    protected final void addAllAny(List<Object> anys) {
        if(anys != null) {
            while(anys.contains(null)){
                anys.remove(null);
            }
            this.anys.addAll(anys);
        }
    }

    /**
     * Removes a descriptive elements.
     * @param any A descriptive element.
     */
    protected final void removeAny(Object any) {
        this.anys.remove(any);
    }

    /**
     * Removes a list of descriptive elements.
     * @param anys List of descriptive elements.
     */
    protected final void removeAllAny(List<Object> anys) {
        if(anys != null) {
            this.anys.removeAll(anys);
        }
    }

    /**
     * Sets the list of the descriptive elements.
     * @param anys A list of the descriptive elements.
     */
    protected final void setAnys(List<Object> anys) {
        if(anys != null) {
            while(anys.contains(null)){
                anys.remove(null);
            }
            this.anys = new ArrayList<>();
            this.anys.addAll(anys);
        }
        else{
            this.anys = null;
        }
    }
}
