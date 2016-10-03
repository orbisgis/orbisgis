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

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;
import org.orbisgis.wpsservice.LocalWpsServerImpl;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataField", propOrder = {"dataStoreIdentifier", "fieldTypeList",
        "excludedTypeList", "listFieldValue", "multiSelection"})
public class DataField extends ComplexDataType {

    /** Identifier of the parent DataStore */
    @XmlElement(name = "DataStoreId", namespace = "http://orbisgis.org")
    private URI dataStoreIdentifier;
    /** Indicates if the DataField should be reloaded because of a modification of the parent DataStore.*/
    @XmlTransient
    private boolean isSourceModified = true;
    /** List of type accepted for the field.*/
    @XmlElement(name = "FieldType", namespace = "http://orbisgis.org")
    private List<DataType> fieldTypeList;
    /** List of type excluded for the field.*/
    @XmlElement(name = "ExcludedType", namespace = "http://orbisgis.org")
    private List<DataType> excludedTypeList;
    /** List of FieldValue liked to the DataField */
    @XmlElement(name = "FieldValue", namespace = "http://orbisgis.org")
    private List<FieldValue> listFieldValue;
    /** Indicates if the use can choose more than one field*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection = false;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LocalWpsServerImpl.class);

    /**
     * Main constructor.
     * @param formatList Formats of the data accepted.
     * @param fieldTypeList List of the type accepted for this field.
     * @param dataStoreURI Identifier of the parent dataStore.
     * @throws MalformedScriptException
     */
    public DataField(List<Format> formatList, List<DataType> fieldTypeList, URI dataStoreURI) throws MalformedScriptException {
        format = formatList;
        listFieldValue = new ArrayList<>();
        this.fieldTypeList = fieldTypeList;
        this.dataStoreIdentifier = dataStoreURI;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected DataField(){
        super();
        fieldTypeList = null;
        excludedTypeList = null;
        listFieldValue = null;
        dataStoreIdentifier = null;
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
        if(listFieldValue != null) {
            for (FieldValue fieldValue : listFieldValue) {
                fieldValue.setDataStoreModified(isSourceModified);
            }
        }
    }

    /**
     * Returns the list of valid type for the field.
     * @return List of accepted DataType.
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

    /**
     * Sets the list of excluded type for the field.
     * @param excludedTypeList List of excluded DataType.
     */
    public void setExcludedTypeList(List<DataType> excludedTypeList) throws MalformedScriptException {
        for(DataType excludedType : excludedTypeList){
            for(DataType dataType : fieldTypeList){
                if(excludedType.equals(dataType)){
                    throw new MalformedScriptException(DataField.class, "excludedTypeList", I18N.tr("A same DataType is" +
                            " accepted and excluded."));
                }
            }
        }
        this.excludedTypeList = excludedTypeList;
    }

    /**
     * Returns the list of excluded type for the field.
     * @return List of excluded DataType.
     */
    public List<DataType> getExcludedTypeList() {
        return excludedTypeList;
    }

    /**
     * Returns true if the user can select more than one field, false otherwise.
     * @return True if the user can select more than one field, false otherwise.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than one field or not.
     * @param multiSelection True if the user can select more than one field, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }
}
