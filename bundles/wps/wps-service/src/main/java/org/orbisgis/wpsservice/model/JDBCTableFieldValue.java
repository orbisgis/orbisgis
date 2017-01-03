/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

/**
 * JDBCTableFieldValue represent one or more values from a JDBCTableField.
 * @author Sylvain PALOMINOS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JDBCTableFieldValue", propOrder = {"dataFieldIdentifier", "jdbcTableIdentifier", "multiSelection"})
public class JDBCTableFieldValue extends ComplexDataType {

    /** Identifier of the 'parent' JDBCTableField */
    @XmlElement(name = "JDBCTableFieldId", namespace = "http://orbisgis.org")
    private URI jdbcTableFieldIdentifier;
    /** Indicates if the JDBCTableFieldValue should be reloaded because of a modification of the parent JDBCTableField.*/
    @XmlTransient
    private boolean isJDBCTableFieldModified = true;
    /** Identifier of the 'parent' JDBCTable */
    @XmlElement(name = "JDBCTableId", namespace = "http://orbisgis.org")
    private URI jdbcTableIdentifier;
    /** Indicates if the JDBCTableFieldValue should be reloaded because of a modification of the parent JDBCTable.*/
    @XmlTransient
    private boolean isJDBCTableModified = true;
    /** Enable the selection of more than one value if true.*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection;

    /**
     * Main constructor
     * @param formatList Formats of the data accepted.
     * @param jdbcTableFieldIdentifier Identifier of the 'parent' JDBCTableField.
     * @param multiSelection Enable or not the selection of more than one value.
     * @throws MalformedScriptException
     */
    public JDBCTableFieldValue(List<Format> formatList, URI jdbcTableFieldIdentifier, boolean multiSelection)
            throws MalformedScriptException {
        format = formatList;
        this.multiSelection = multiSelection;
        this.jdbcTableFieldIdentifier = jdbcTableFieldIdentifier;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected JDBCTableFieldValue(){
        super();
        jdbcTableFieldIdentifier = null;
        jdbcTableIdentifier = null;
    }

    /**
     * Sets the URI of the parent JDBCTableField.
     * @param jdbcTableFieldIdentifier URI of the JDBCTableField.
     */
    public void setJDBCTableFieldIdentifierIdentifier(URI jdbcTableFieldIdentifier){
        this.jdbcTableFieldIdentifier = jdbcTableFieldIdentifier;
    }

    /**
     * Returns the JDBCTableField identifier.
     * @return The JDBCTableField identifier.
     */
    public URI getJDBCTableFieldIdentifier(){
        return jdbcTableFieldIdentifier;
    }

    /**
     * Tells if the parent JDBCTableField has been modified since last time it was checked.
     * @return True if the parent JDBCTableField has been modified, false otherwise.
     */
    public boolean isJDBCTableFieldModified() {
        return isJDBCTableFieldModified;
    }

    /**
     * Sets if the parent JDBCTableField has been modified
     * @param isJDBCTableFieldModified True if the parent JDBCTableField has been modified, false otherwise.
     */
    public void setJDBCTableFieldModified(boolean isJDBCTableFieldModified) {
        this.isJDBCTableFieldModified = isJDBCTableFieldModified;
    }


    /**
     * Sets the URI of the parent JDBCTable.
     * @param jdbcTableIdentifier URI of the JDBCTable.
     */
    public void setJDBCTableIdentifier(URI jdbcTableIdentifier){
        this.jdbcTableIdentifier = jdbcTableIdentifier;
    }

    /**
     * Returns the JDBCTable identifier.
     * @return The JDBCTable identifier.
     */
    public URI getJDBCTableIdentifier(){
        return jdbcTableIdentifier;
    }

    /**
     * Tells if the parent JDBCTable has been modified since last time it was checked.
     * @return True if the parent JDBCTableField has been modified, false otherwise.
     */
    public boolean isJDBCTableModified() {
        return isJDBCTableModified;
    }

    /**
     * Sets if the parent JDBCTable has been modified
     * @param isJDBCTableModified True if the parent JDBCTable has been modified, false otherwise.
     */
    public void setJDBCTableModified(boolean isJDBCTableModified) {
        this.isJDBCTableModified = isJDBCTableModified;
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
    public boolean getMultiSelection(){
        return multiSelection;
    }
}
