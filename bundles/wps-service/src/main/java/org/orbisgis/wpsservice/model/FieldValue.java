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

package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * FieldValue represent one or more values from a DataField.
 * @author Sylvain PALOMINOS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FieldValue", propOrder = {"dataFieldIdentifier", "dataStoreIdentifier", "multiSelection"})
public class FieldValue extends ComplexDataType {

    /** Identifier of the 'parent' DataField */
    @XmlElement(name = "DataFieldId", namespace = "http://orbisgis.org")
    private URI dataFieldIdentifier;
    /** Indicates if the FieldValue should be reloaded because of a modification of the parent DataField.*/
    @XmlTransient
    private boolean isDataFieldModified = true;
    /** Identifier of the 'parent' DataStore */
    @XmlElement(name = "DataStoreId", namespace = "http://orbisgis.org")
    private URI dataStoreIdentifier;
    /** Indicates if the FieldValue should be reloaded because of a modification of the parent DataStore.*/
    @XmlTransient
    private boolean isDataStoreModified = true;
    /** Enable the selection of more than one value if true.*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection;

    /**
     * Main constructor
     * @param formatList Formats of the data accepted.
     * @param dataFieldIdentifier Identifier of the 'parent' dataField.
     * @param multiSelection Enable or not the selection of more than one value.
     * @throws MalformedScriptException
     */
    public FieldValue(List<Format> formatList, URI dataFieldIdentifier, boolean multiSelection) throws MalformedScriptException {
        format = formatList;
        this.multiSelection = multiSelection;
        this.dataFieldIdentifier = dataFieldIdentifier;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected FieldValue(){
        super();
        dataFieldIdentifier = null;
        dataStoreIdentifier = null;
    }

    /**
     * Sets the URI of the parent DataField.
     * @param dataFieldIdentifier URI of the DataField.
     */
    public void setDataFieldIdentifier(URI dataFieldIdentifier){
        this.dataFieldIdentifier = dataFieldIdentifier;
    }

    /**
     * Returns the DataField identifier.
     * @return The DataField identifier.
     */
    public URI getDataFieldIdentifier(){
        return dataFieldIdentifier;
    }

    /**
     * Tells if the parent DataField has been modified since last time it was checked.
     * @return True if the parent DataField has been modified, false otherwise.
     */
    public boolean isDataFieldModified() {
        return isDataFieldModified;
    }

    /**
     * Sets if the parent DataField has been modified
     * @param isDataFieldModified True if the parent DataField has been modified, false otherwise.
     */
    public void setDataFieldModified(boolean isDataFieldModified) {
        this.isDataFieldModified = isDataFieldModified;
    }


    /**
     * Sets the URI of the parent DataStore.
     * @param dataStoreIdentifier URI of the DataStore.
     */
    public void setDataStoredIdentifier(URI dataStoreIdentifier){
        this.dataStoreIdentifier = dataStoreIdentifier;
    }

    /**
     * Returns the DataStore identifier.
     * @return The DataStore identifier.
     */
    public URI getDataStoreIdentifier(){
        return dataStoreIdentifier;
    }

    /**
     * Tells if the parent DataStore has been modified since last time it was checked.
     * @return True if the parent DataField has been modified, false otherwise.
     */
    public boolean isDataStoreModified() {
        return isDataStoreModified;
    }

    /**
     * Sets if the parent DataStore has been modified
     * @param isDataStoreModified True if the parent DataStore has been modified, false otherwise.
     */
    public void setDataStoreModified(boolean isDataStoreModified) {
        this.isDataStoreModified = isDataStoreModified;
    }

    /**
     * Set if more than one value can be selected.
     * @param multiSelection True if more than one value can be selected, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection){
        this.multiSelection = multiSelection;
    }

    /**
     * Tells if more than one value can be selected.
     * @return True if more than one value can be selected, false otherwise.
     */
    public boolean getMuliSelection(){
        return multiSelection;
    }
/*
    @Override
    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy2 strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!super.equals(thisLocator, thatLocator, object, strategy)) {
            return false;
        }
        final FieldValue that = ((FieldValue) object);
        {
            if( (this.getMuliSelection() != that.getMuliSelection()) ||
                    !Objects.equals(this.getDataFieldIdentifier(), that.getDataFieldIdentifier()) ||
                    !Objects.equals(this.getDataStoreIdentifier(), that.getDataStoreIdentifier()) )
                return false;
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy2 strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }*/
}
