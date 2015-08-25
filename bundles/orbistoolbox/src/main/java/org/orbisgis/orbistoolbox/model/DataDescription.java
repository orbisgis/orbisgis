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
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public DataDescription(Format format) throws MalformedScriptException {
        if(format == null){
            throw new MalformedScriptException(this.getClass(), "format", "can not be null");
        }
        format.setDefaultFormat(true);
        this.formats = new ArrayList<>();
        this.formats.add(format);
    }

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null format list containing a default format.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public DataDescription(List<Format> formatList)  throws MalformedScriptException {
        if(formatList == null){
            throw new MalformedScriptException(this.getClass(), "formatList", "can not be null");
        }
        if(formatList.isEmpty()){
            throw new MalformedScriptException(this.getClass(), "formatList", "can not be empty");
        }
        if(formatList.contains(null)){
            throw new MalformedScriptException(this.getClass(), "formatList", "can not contain a null value");
        }
        boolean flag = false;

        //Verify that the Format list contains exactly one default format
        for(Format format : formatList) {
            if(flag && format.isDefaultFormat()){
                throw new MalformedScriptException(this.getClass(), "formatList", "can only contain one default Format");
            }
            if(format.isDefaultFormat()){
                flag = true;
            }
        }
        if(!flag){
            throw new MalformedScriptException(this.getClass(), "formatList", "should contain a default format");
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
    public final List<Format> getFormats() {
        return formats;
    }

    /**
     * Adds a format to the list following conditions :
     * - The format is not null
     * - The format is not already in the list
     * - Only one format is set as the default one
     *
     * @param format Format to add.
     * @throws MalformedScriptException Exception get on setting a format which is null or
     * setting more than one default format.
     */
    protected final void addFormat(Format format) throws MalformedScriptException {
        if(this.formats.contains(format)){
            return;
        }
        if(format == null){
            throw new MalformedScriptException(this.getClass(), "format", "can not be null");
        }
        if (this.formats == null) {
            if(!format.isDefaultFormat()){
                throw new MalformedScriptException(this.getClass(), "format", "should be the default format");
            }
            this.formats = new ArrayList<>();
        }
        if(format.isDefaultFormat()) {
            for (Format f : formats) {
                if (f.isDefaultFormat()) {
                    throw new MalformedScriptException(this.getClass(), "format", "can only contain one default format");
                }
            }
        }
        this.formats.add(format);
    }

    /**
     * Removes a format from the list except if it is the last one or the default one.
     *
     * @param format Format to remove.
     * @throws MalformedScriptException Exception get on removing the last format or the default one
     */
    protected final void removeFormat(Format format) throws MalformedScriptException {
        if (this.formats == null ||
                (this.formats.size() == 1 && this.formats.contains(format)) ||
                format.isDefaultFormat()) {
            throw new MalformedScriptException(this.getClass(), "format", "should contain a default format");
        }
        this.formats.remove(format);
    }

    /**
     * Sets the list of format with the given argument.
     * The argument can not be null or empty or containing two formats sets as default.
     *
     * @param formatList New list of formats.
     * @throws MalformedScriptException Exception get on setting a list of format which is null or empty or
     * containing more than one default format.
     */
    protected final void setFormats(List<Format> formatList) throws MalformedScriptException {
        if(formatList == null) {
            throw new MalformedScriptException(this.getClass(), "formatList", "can not be null");
        }
        if(formatList.isEmpty()) {
            throw new MalformedScriptException(this.getClass(), "formatList", "can not be empty");
        }
        while(formatList.contains(null)){
            formatList.remove(null);
        }
        boolean hasDefault = false;

        //Verify that the Format list contain exactly one default format
        for(Format format : formatList) {
            if(hasDefault && format.isDefaultFormat()){
                throw new MalformedScriptException(this.getClass(), "formatList", "can only contain one default Format");
            }
            if(format.isDefaultFormat()){
                hasDefault = true;
            }
        }

        if(!hasDefault){
            throw new MalformedScriptException(this.getClass(), "formatList", "should contain a default Format");
        }

        this.formats = formatList;
    }

    /**
     * Sets the given format as the default one.
     * @param format Not null new default format.
     * @throws MalformedScriptException Exception get on setting a null or a not contained format as the default one.
     */
    protected final void setDefaultFormat(Format format) throws MalformedScriptException {
        if(format == null){
            throw new MalformedScriptException(this.getClass(), "format", "can not be null");
        }
        if(!this.formats.contains(format)) {
            throw new MalformedScriptException(this.getClass(), "format", "is not contained by the format list");
        }
        for(Format f : formats){
            f.setDefaultFormat(false);
        }
        format.setDefaultFormat(true);
    }
}
