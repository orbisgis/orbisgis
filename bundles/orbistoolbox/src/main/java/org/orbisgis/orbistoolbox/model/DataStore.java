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

package org.orbisgis.orbistoolbox.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DataStore represent a data source which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStore extends ComplexData{
    /** True if the data is spatial, false otherwise **/
    private boolean isSpatial;
    /** True if the data comes from the OrbisGIS geocatalog spatial, false otherwise **/
    private boolean isGeocatalog;
    /** True if the data is a file, false otherwise **/
    private boolean isFile;
    /** True if the data comes from a dataBase, false otherwise **/
    private boolean isDataBase;
    /** List of DataField liked to the DataStore */
    private List<DataField> listDataField;

    public DataStore(List<Format> formatList) throws MalformedScriptException {
        super(formatList);
        listDataField = new ArrayList<>();
    }

    public void setSpatial(boolean isSpatial){
        this.isSpatial = isSpatial;
    }

    public boolean isSpatial(){
        return isSpatial;
    }

    public boolean isGeocatalog() {
        return isGeocatalog;
    }

    public void setIsGeocatalog(boolean isGeocatalog) {
        this.isGeocatalog = isGeocatalog;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public boolean isDataBase() {
        return isDataBase;
    }

    public void setIsDataBase(boolean isDataBase) {
        this.isDataBase = isDataBase;
    }

    public void addDataField(DataField dataField){
        this.listDataField.add(dataField);
    }

    public List<DataField> getListDataField(){
        return listDataField;
    }
}
