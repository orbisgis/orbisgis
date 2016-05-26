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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * DataStore represent a data source which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataStore", propOrder = {"isSpatial", "listDataField", "isAutoImport"})
public class DataStore extends ComplexDataType {
    /**DataStore types.*/
    public static final String DATASTORE_TYPE_GEOCATALOG = "DATASTORE_TYPE_GEOCATALOG";
    public static final String DATASTORE_TYPE_FILE = "DATASTORE_TYPE_FILE";

    /** True if the data is spatial, false otherwise **/
    @XmlAttribute(name = "isSpatial")
    private boolean isSpatial;
    /** List of DataField liked to the DataStore */
    @XmlElement(name = "DataField", namespace = "http://orbisgis.org")
    private List<DataField> listDataField;
    /** True if the toolBox should load the file or just give the file path. */
    @XmlAttribute(name = "isAutoImport")
    private boolean isAutoImport;

    /**
     * Main constructor
     * @param formatList List of formats accepted.
     * @throws MalformedScriptException
     */
    public DataStore(List<Format> formatList) throws MalformedScriptException {
        format = formatList;
        listDataField = new ArrayList<>();
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected DataStore(){
        super();
        listDataField = null;
    }

    public void setAutoImport(boolean isAutoImport){
        this.isAutoImport = isAutoImport;
    }

    public boolean isAutoImport(){
        return isAutoImport;
    }

    /**
     * Sets if the data should be spatial or no matter.
     * @param isSpatial True if the data should be spatial, false if no matter.
     */
    public void setIsSpatial(boolean isSpatial){
        this.isSpatial = isSpatial;
    }

    /**
     * Tells if the data should be spatial or if no matter.
     * @return True if the data should be spatial, false if no matter.
     */
    public boolean isSpatial(){
        return isSpatial;
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
}
