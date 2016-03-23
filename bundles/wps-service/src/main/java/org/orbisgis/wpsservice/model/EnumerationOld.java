/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

/**
 * Enumeration model class
 * @author Sylvain PALOMINOS
 **/

public class EnumerationOld extends ComplexData{

    /** List of values.*/
    private String[] values;
    /** List of values names.*/
    private String[] names;
    /** Default values.*/
    private String[] defaultValues;
    /** Enable or not the selection of more than one value.*/
    private boolean multiSelection = false;
    /** Enable or not the user to use its own value.*/
    private boolean isEditable = false;

    /**
     * Main constructor.
     * @param format Format of the data accepted.
     * @param valueList List of values.
     * @param defaultValues Default value. If null, no default value.
     * @throws MalformedScriptException
     */
    public EnumerationOld(Format format, String[] valueList, String[] defaultValues) throws MalformedScriptException {
        super(format);
        this.values = valueList;
        this.defaultValues = defaultValues;
    }

    /**
     * Returns the list of the enumeration value.
     * @return The list of values.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Returns the default values.
     * @return The default values.
     */
    public String[] getDefaultValues() {
        return defaultValues;
    }

    /**
     * Returns true if more than one value can be selected, false otherwise.
     * @return True if more than one value can be selected, false otherwise.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than one value.
     * @param multiSelection True if more than one value can be selected, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    /**
     * Returns true if the user can use a custom value, false otherwise.
     * @return True if the user can use a custom value, false otherwise.
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets if the user can use a custom value.
     * @param editable True if the user can use a custom value, false otherwise.
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setValuesNames(String[] names){
        this.names = names;
    }

    public String[] getValuesNames(){
        return names;
    }
}
