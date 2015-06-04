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
    public DataDescription(Format format) {
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
     * @param formatList Not null default format.
     * @throws IllegalArgumentException Exception get on setting a format which is null or is not the default one.
     */
    public DataDescription(List<Format> formatList) {
        if(formatList == null || formatList.isEmpty()){
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null or empty");
        }
        boolean flag = false;
        List<Format> temp = this.formats;
        this.formats = new ArrayList<>();
        for(Format format : formatList) {
            if(format == null){
                this.formats = temp;
                throw new IllegalArgumentException("A format can not be null");
            }
            if(flag && format.isDefaultFormat()){
                this.formats = temp;
                throw new IllegalArgumentException("Only one format can be set as the default one");
            }
            if(format.isDefaultFormat()){
                flag = true;
            }
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
     * Adds a format to the list if it is not already in.
     *
     * @param format Format to add.
     */
    public void addFormat(Format format) {
        if(format == null){
            throw new IllegalArgumentException("The parameter \"format\" can not be null");
        }
        if (this.formats == null) {
            this.formats = new ArrayList<>();
        }
        if (!this.formats.contains(format)) {
            formats.add(format);
        }
    }

    /**
     * Adds a list of format. If a format was already added, does nothing.
     *
     * @param formats List of keywords to add.
     */
    public void addAllFormat(List<Format> formats) {
        for (Format f : formats) {
            addFormat(f);
        }
    }

    /**
     * Removes a format from the list except if it is the last one.
     *
     * @param format Format to remove.
     */
    public void removeFormat(Format format) {
        if (this.formats == null || (this.formats.size() == 1 && this.formats.contains(format))) {
            this.formats.remove(format);
        }
    }

    /**
     * Removes a list of format except if the last format
     *
     * @param formats List of formats to remove.
     */
    public void removeAllFormat(List<Format> formats) {
        for (Format f : formats) {
            removeFormat(f);
        }
    }

    /**
     * Sets the list of format.
     *
     * @param formats New list of formats.
     */
    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }
}
