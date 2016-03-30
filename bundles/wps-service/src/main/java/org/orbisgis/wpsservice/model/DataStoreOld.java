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
 * DataStore represent a data source which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/
@Deprecated
public class DataStoreOld extends ComplexData {
    /**DataStore types.*/
    public static final String DATASTORE_TYPE_GEOCATALOG = "DATASTORE_TYPE_GEOCATALOG";
    public static final String DATASTORE_TYPE_FILE = "DATASTORE_TYPE_FILE";

    /** True if the data is spatial, false otherwise **/
    private boolean isSpatial;
    /** True if the data can come from the OrbisGIS geocatalog spatial, false otherwise **/
    private boolean isGeocatalog;
    /** True if the data can be a file, false otherwise **/
    private boolean isFile;
    /** True if the data can come from an external dataBase, false otherwise **/
    private boolean isDataBase;
    /** List of DataField liked to the DataStore */
    private List<DataFieldOld> listDataField;
    /** True if the toolBox should load the file or just give the file path. */
    private boolean autoImport;

    /**
     * Main constructor
     * @param formatList List of formats accepted.
     * @throws MalformedScriptException
     */
    public DataStoreOld(List<Format> formatList) throws MalformedScriptException {
        super(formatList);
        listDataField = new ArrayList<>();
    }

    public void setAutoImport(boolean autoImport){
        this.autoImport = autoImport;
    }

    public boolean isAutoImport(){
        return autoImport;
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
     * Tells if the data can come from the OrbisGIS geocatalog.
     * @return True if the data can come from the geocatalog, false otherwise.
     */
    public boolean isGeocatalog() {
        return isGeocatalog;
    }

    /**
     * Sets if the data can come from the geocatalog or not.
     * @param isGeocatalog True if the data can come from the geocatalog, false otherwise.
     */
    public void setIsGeocatalog(boolean isGeocatalog) {
        this.isGeocatalog = isGeocatalog;
    }

    /**
     * Tells if the data can be a file.
     * @return True if the data can be a file.
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Sets if the data can be a file.
     * @param isFile True if the data can be a file, false otherwise.
     */
    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    /**
     * Tells if the data can come from an external database.
     * @return True if the data can come from an external database.
     */
    public boolean isDataBase() {
        return isDataBase;
    }

    /**
     * Sets if the data can come from an external database or not.
     * @param isDataBase True if the data can come from an external database, false otherwise.
     */
    public void setIsDataBase(boolean isDataBase) {
        this.isDataBase = isDataBase;
    }

    /**
     * Adds a DataField as a 'child' of the DataStore.
     * @param dataField DataField to add.
     */
    public void addDataField(DataFieldOld dataField){
        this.listDataField.add(dataField);
    }

    /**
     * Return the list of 'child' DataField.
     * @return List of DataField.
     */
    public List<DataFieldOld> getListDataField(){
        return listDataField;
    }

    /**
     * Build an URI usable in the wps service from the dataStore information.
     * @param dataStoreType Type of the dataStore, can be DATASTORE_TYPE_GEOCATALOG or DATASTORE_TYPE_FILE.
     * @param dataSource Body of the uri. If it is a file, dataSource is the file absolute path, if it is a geocatalog,
     *                   dataSource is the table name.
     * @param tableName Table name.
     * @return
     */
    public static URI buildUriDataStore(String dataStoreType, String dataSource, String tableName){
        String uri = "";
        switch(dataStoreType){
            case DATASTORE_TYPE_GEOCATALOG:
                uri += "geocatalog:";
                break;
            case DATASTORE_TYPE_FILE:
                uri += "file:";
        }
        uri += dataSource;
        uri += "#"+tableName;
        return URI.create(uri);
    }
}
