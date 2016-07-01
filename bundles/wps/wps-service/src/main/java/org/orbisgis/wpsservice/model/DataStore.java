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

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * DataStore represent a data source which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataStore", propOrder = {"dataStoreTypeList", "excludedTypeList", "listDataField"})
public class DataStore extends ComplexDataType {
    /** List of field type that should be contained by the DataStore.*/
    @XmlElement(name = "DataStoreType", namespace = "http://orbisgis.org")
    private List<DataType> dataStoreTypeList;
    /** List of field type forbidden for the DataSTore. If the DataStore contains the type, it won't be available.*/
    @XmlElement(name = "ExcludedType", namespace = "http://orbisgis.org")
    private List<DataType> excludedTypeList;
    /** List of DataField liked to the DataStore */
    @XmlElement(name = "DataField", namespace = "http://orbisgis.org")
    private List<DataField> listDataField;

    /**
     * Main constructor
     * @param formatList List of formats accepted.
     * @throws MalformedScriptException
     */
    public DataStore(List<Format> formatList) throws MalformedScriptException {
        format = formatList;
        dataStoreTypeList = new ArrayList<>();
        excludedTypeList = new ArrayList<>();
        listDataField = new ArrayList<>();
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected DataStore(){
        super();
        listDataField = null;
    }

    /**
     * Adds a DataField as a 'child' of the DataStore.
     * @param dataField DataField to add.
     */
    public void addDataField(DataField dataField){
        this.listDataField.add(dataField);
    }

    /**
     * Return the list of 'child' DataField.
     * @return List of DataField.
     */
    public List<DataField> getListDataField(){
        return listDataField;
    }

    /**
     * Sets the list of types that should be contained by the DataStore.
     * @param dataStoreTypeList List of DataType.
     */
    public void setDataStoreTypeList(List<DataType> dataStoreTypeList) throws MalformedScriptException {
        for(DataType dataStoreType : dataStoreTypeList){
            for(DataType excludedType : excludedTypeList){
                if(dataStoreType.equals(excludedType)){
                    throw new MalformedScriptException(DataField.class, "dataStoreTypeList", "A same DataType is" +
                            " accepted and excluded");
                }
            }
        }
        this.dataStoreTypeList = dataStoreTypeList;
    }

    /**
     * Returns the list of types that should be contained by the DataStore.
     * @return List of DataType.
     */
    public List<DataType> getDataStoreTypeList() {
        return dataStoreTypeList;
    }

    /**
     * Sets the list of excluded type for the DataStore.
     * @param excludedTypeList List of excluded DataType.
     */
    public void setExcludedTypeList(List<DataType> excludedTypeList) throws MalformedScriptException {
        for(DataType excludedType : excludedTypeList){
            for(DataType dataType : dataStoreTypeList){
                if(excludedType.equals(dataType)){
                    throw new MalformedScriptException(DataField.class, "excludedTypeList", "A same DataType is" +
                            " accepted and excluded");
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
}
