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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * RawData extends the ComplexData class.
 * It represents a file or a folder.
 *
 * @author Sylvain PALOMINOS
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RawData", propOrder = {"isFile", "isDirectory", "multiSelection"})
public class RawData extends ComplexDataType {

    /** True if the RawData can be a file, false otherwise. */
    @XmlAttribute(name = "isFile")
    private boolean isFile;
    /** True if the RawData can be a directory, false otherwise. */
    @XmlAttribute(name = "isDirectory")
    private boolean isDirectory;
    /** True if the user can select more than one file/directory, false otherwise. */
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection;

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null default format.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public RawData(List<Format> formatList) throws MalformedScriptException {
        this.format = formatList;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected RawData(){
        super();
    }

    /**
     * Returns if the RawData can be a directory or not.
     * @return True if the RawData can be a directory, false otherwise.
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Sets if the RawData can be a directory or not.
     * @param directory True if the RawData can be a directory, false otherwise.
     */
    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    /**
     * Returns if the RawData can be a file or not.
     * @return True if the RawData can be a file, false otherwise.
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Sets if the RawData can be a file or not.
     * @param file True if the RawData can be a file, false otherwise.
     */
    public void setFile(boolean file) {
        isFile = file;
    }

    /**
     * Returns if the user can select more than just one file/directory.
     * @return True if user can select more than just one file/directory, false otherwise.
     */
    public boolean multiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than just one file/directory.
     * @param multiSelection True if user can select more than just one file/directory, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }
}
