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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

public class DataField extends ComplexData{

    /** Identifier of the parent DataStore */
    private URI dataStoreIdentifier;
    /** Indicates if the DataField should be reloaded because of a modification of the parent DataStore.*/
    private boolean isSourceModified = false;
    /** List of type accepted for the field.*/
    private List<DataType> fieldTypeList;
    /** List of FieldValue liked to the DataField */
    private List<FieldValue> listFieldValue;

    /**
     * Main constructor.
     * @param format Format of the data accepted.
     * @param fieldTypeList List of the type accepted for this field.
     * @param dataStoreURI Identifier of the parent dataStore.
     * @throws MalformedScriptException
     */
    public DataField(Format format, List<DataType> fieldTypeList, URI dataStoreURI) throws MalformedScriptException {
        super(format);
        listFieldValue = new ArrayList<>();
        this.fieldTypeList = fieldTypeList;
        this.dataStoreIdentifier = dataStoreURI;
    }

    /**
     * Returns the identifier of the parent DataStore.
     * @return The identifier of the DataStore.
     */
    public URI getDataStoreIdentifier(){
        return dataStoreIdentifier;
    }

    /**
     * Tells if the parent DataStore has been modified since last time it was checked.
     * @return True if the parent DataStore has been modified, false otherwise.
     */
    public boolean isSourceModified() {
        return isSourceModified;
    }

    /**
     * Sets if the parent dataStore has been modified
     * @param isSourceModified True if the parent DataStore has been modified, false otherwise.
     */
    public void setSourceModified(boolean isSourceModified) {
        this.isSourceModified = isSourceModified;
        for(FieldValue fieldValue : listFieldValue){
            fieldValue.setDataStoreModified(isSourceModified);
        }
    }

    /**
     * Returns the list of valid type for the field.
     * @return List of valie FieldType.
     */
    public List<DataType> getFieldTypeList() {
        return fieldTypeList;
    }

    /**
     * Adds a FieldValue as a 'child' of the DataField.
     * @param fieldValue FieldValue to add.
     */
    public void addFieldValue(FieldValue fieldValue){
        this.listFieldValue.add(fieldValue);
    }

    /**
     * Return the list of 'child' FieldValue.
     * @return List of FieldValue.
     */
    public List<FieldValue> getListFieldValue(){
        return listFieldValue;
    }
}
