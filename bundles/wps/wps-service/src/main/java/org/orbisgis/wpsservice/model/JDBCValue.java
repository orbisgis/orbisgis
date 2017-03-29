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
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
 * JDBCValue represent one or more values from a JDBCColumn.
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JDBCValue", propOrder = {"jdbcColumnIdentifier", "jdbcTableIdentifier", "multiSelection",
        "defaultValues"})
public class JDBCValue extends ComplexDataType {

    /** Identifier of the 'parent' JDBCColumn */
    @XmlElement(name = "JDBCColumnId", namespace = "http://orbisgis.org")
    private URI jdbcColumnIdentifier;
    /** Indicates if the JDBCValue should be reloaded because of a modification of the parent JDBCColumn.*/
    @XmlTransient
    private boolean isJDBCColumnModified = true;
    /** Identifier of the 'parent' JDBCTable */
    @XmlElement(name = "JDBCTableId", namespace = "http://orbisgis.org")
    private URI jdbcTableIdentifier;
    /** Indicates if the JDBCValue should be reloaded because of a modification of the parent JDBCTable.*/
    @XmlTransient
    private boolean isJDBCTableModified = true;
    /** Enable the selection of more than one value if true.*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection;
    /** Default values of the JDBCValue. */
    @XmlAttribute(name = "defaultValues")
    private String[] defaultValues;

    /**
     * Main constructor
     * @param formatList Formats of the data accepted.
     * @param jdbcColumnIdentifier Identifier of the 'parent' JDBCColumn.
     * @param multiSelection Enable or not the selection of more than one value.
     * @throws MalformedScriptException
     */
    public JDBCValue(List<Format> formatList, URI jdbcColumnIdentifier, boolean multiSelection)
            throws MalformedScriptException {
        format = formatList;
        this.multiSelection = multiSelection;
        this.jdbcColumnIdentifier = jdbcColumnIdentifier;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected JDBCValue(){
        super();
        jdbcColumnIdentifier = null;
        jdbcTableIdentifier = null;
    }

    /**
     * Sets the URI of the parent JDBCColumn.
     * @param jdbcColumnIdentifier URI of the JDBCColumn.
     */
    public void setJDBCTableFieldIdentifierIdentifier(URI jdbcColumnIdentifier){
        this.jdbcColumnIdentifier = jdbcColumnIdentifier;
    }

    /**
     * Returns the JDBCColumn identifier.
     * @return The JDBCColumn identifier.
     */
    public URI getJDBCColumnIdentifier(){
        return jdbcColumnIdentifier;
    }

    /**
     * Tells if the parent JDBCColumn has been modified since last time it was checked.
     * @return True if the parent JDBCColumn has been modified, false otherwise.
     */
    public boolean isJDBCColumndModified() {
        return isJDBCColumnModified;
    }

    /**
     * Sets if the parent JDBCTableField has been modified
     * @param isJDBCTableFieldModified True if the parent JDBCTableField has been modified, false otherwise.
     */
    public void setJDBCTableFieldModified(boolean isJDBCTableFieldModified) {
        this.isJDBCColumnModified = isJDBCTableFieldModified;
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
    public boolean isMultiSelection(){
        return multiSelection;
    }

    /**
     * Sets the default values of the geometry.
     * @param defaultValues Default values of the geometry.
     */
    public void setDefaultValues(String[] defaultValues){
        this.defaultValues = defaultValues;
    }

    /**
     * Returns the default values of the geometry.
     * @return The default values of the geometry.
     */
    public String[] getDefaultValues(){
        return defaultValues;
    }
}
