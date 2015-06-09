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

import java.util.ArrayList;
import java.util.List;

/**
 * The DataDescription structure contains basic properties for defining data inputs and outputs, including mimetype,
 * encoding and schema.
 * These properties specify supported formats for input and output data of computing processes.
 * Any input or output item may support multiple formats, one of which is the default format.
 * Processes may require that an input or output data set does not exceed a certain data volume.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#20
 *
 * @author Sylvain PALOMINOS
 */

public abstract class DataDescription {

    /** List of supported formats */
    private List<Format> formats;

    /**
     * Constructor giving the default format.
     * The Format can not be null and should be set as the default one.
     * @param format Not null default format.
     * @throws IllegalArgumentException Exception get on setting a format which is null or is not the default one.
     */
    public DataDescription(Format format) throws IllegalArgumentException {
        if(format == null){
            throw new IllegalArgumentException("The parameter \"format\" can not be null");
        }
        if(!format.isDefaultFormat()){
            throw new IllegalArgumentException("The parameter \"format\" should be the default format");
        }
        this.formats = new ArrayList<>();
        this.formats.add(format);
    }

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null format list containing a default format.
     * @throws IllegalArgumentException Exception get on setting a format which is null or is not the default one.
     */
    public DataDescription(List<Format> formatList)  throws IllegalArgumentException {
        if(formatList == null || formatList.isEmpty() || formatList.contains(null)){
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null or empty or " +
                    "containing a null value");
        }
        boolean flag = false;

        for(Format format : formatList) {
            if(flag && format.isDefaultFormat()){
                throw new IllegalArgumentException("Only one format can be set as the default one");
            }
            if(format.isDefaultFormat()){
                flag = true;
            }
        }

        this.formats = new ArrayList<>();

        for(Format format : formatList) {
            this.formats.add(format);
        }
    }

    /**
     * Returns the list of format. Returns null if it is empty.
     *
     * @return The list of format.
     */
    public List<Format> getFormats() {
        return formats;
    }

    /**
     * Adds a format to the list following conditions :
     * - The format is not null
     * - The format is not already in the list
     * - Only one format is set as the default one
     *
     * @param format Format to add.
     * @throws IllegalArgumentException Exception get on setting a format which is null or
     * setting more than one default format.
     */
    protected void addFormat(Format format) throws IllegalArgumentException {
        if(format == null){
            throw new IllegalArgumentException("The parameter \"format\" can not be null");
        }
        if (this.formats == null) {
            if(!format.isDefaultFormat()){
                throw new IllegalArgumentException("The parameter \"format\" should be the default format");
            }
            this.formats = new ArrayList<>();
        }
        if(format.isDefaultFormat()) {
            for (Format f : formats) {
                if (f.isDefaultFormat()) {
                    throw new IllegalArgumentException("Only one format can be set as the default one");
                }
            }
        }
        if (!this.formats.contains(format)) {
            formats.add(format);
        }
    }

    /**
     * Removes a format from the list except if it is the last one or the default one.
     *
     * @param format Format to remove.
     * @throws IllegalArgumentException Exception get on removing the last format or the default one
     */
    protected void removeFormat(Format format) throws IllegalArgumentException {
        if (this.formats == null ||
                (this.formats.size() == 1 && this.formats.contains(format)) ||
                format.isDefaultFormat()) {
            throw new IllegalArgumentException("Can not remove the last format or the default one");
        }
        this.formats.remove(format);
    }

    /**
     * Sets the list of format with the given argument.
     * The argument can not be null or empty or containing two formats sets as default.
     *
     * @param formatList New list of formats.
     * @throws IllegalArgumentException Exception get on setting a list of format which is null or empty or
     * containing more than one default format.
     */
    protected void setFormats(List<Format> formatList) throws IllegalArgumentException {
        if(formats == null || formats.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"format\" can not be null or empty");
        }
        while(formats.contains(null)){
            formats.remove(null);
        }
        boolean hasDefault = false;

        for(Format format : formatList) {
            if(hasDefault && format.isDefaultFormat()){
                throw new IllegalArgumentException("Only one format can be set as the default one");
            }
            if(format.isDefaultFormat()){
                hasDefault = true;
            }
        }
        this.formats = formatList;
    }
}
