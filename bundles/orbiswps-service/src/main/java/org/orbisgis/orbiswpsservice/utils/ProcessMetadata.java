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
package org.orbisgis.orbiswpsservice.utils;

/**
 * This class contains the process metadata used by OrbisGIS. A process metadata is composed of a role and a title.
 * OrbisGIS use the metadata to get annexe information about the process such as the DBMS compatible with the script.
 * The metadata role corresponds to the property and the title is the value. For example for the compatible DBMS
 * metadata, the role is DBMS_TYPE and the title can be H2GIS or POSTGIS.
 * In this class, the metadata role are the enumeration name and the titles are the enumeration values.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessMetadata {

    /**
     * The DBMS type metadata. It defines with which DBMS the script is compatible. If in the script this metadata isn't
     * present, it means that all the DBMS are compatible.
     *
     * Usage example :
     * @ Process(
     *      ...
     *      metadata = [
     *          @ MetadataAttribute(title="H2GIS", role ="DBMS_TYPE", href = "http://www.h2gis.org/"),
     *          @ MetadataAttribute(title="POSTGIS", role ="DBMS_TYPE", href = "http://postgis.net/")
     *      ]
     *      ...
     * )
     */
    public enum DBMS_TYPE{H2GIS, POSTGIS}
    public static final String DBMS_TYPE_NAME = "DBMS_TYPE";

    /**
     * The internal metadata. Those metadata are used by OrbisGIS for internal purpose such as if the process can be
     * removed from the toolbox, the displayed icon of the process ...
     * Those metadata should not be used in the process definition.
     *
     * Enumeration values meaning :
     *
     *  - IS_REMOVABLE : Indicates if the process can be removed from the toolbox.
     *  - NODE_PATH : Indicates the process node path in the toolbox.
     *  - ICON_ARRAY : Indicates the process icon to use in the toolbox.
     */
    public enum INTERNAL_METADATA{IS_REMOVABLE, NODE_PATH, ICON_ARRAY}
    public static final String INTERNAL_METADATA_NAME = "INTERNAL_METADATA";

    /**
     * The configuration mode metadata. Indicates if the process can be configured only into bash mode (configure more
     * than one process instance at the same time) or only into standard mode (only one process).
     *
     * Enumeration values meaning :
     *
     *  - BASH_MODE_ONLY : Indicates that the process configuration UI can be only shown into bash mode.
     *  - STANDARD_MODE_ONLY : Indicates that the process configuration UI can be only shown into standard mode.
     *
     * Usage example :
     * @ Process(
     *      ...
     *      metadata = [
     *          @ MetadataAttribute(title="BASH_MODE_ONLY", role ="CONFIGURATION_MODE", href = "")
     *      ]
     *      ...
     * )
     */
    public enum CONFIGURATION_MODE{BASH_MODE_ONLY, STANDARD_MODE_ONLY}
    public static final String CONFIGURATION_MODE_NAME = "CONFIGURATION_MODE";

    /**
     * The execution mode metadata. Indicates if the process dan be executed in parallel with other processes instances.
     *
     * Enumeration values meaning :
     *
     *  - PARALLEL_EXECUTION : Indicates that the process can be executed in parallel with other processes instances.
     *
     * Usage example :
     * @ Process(
     *      ...
     *      metadata = [
     *          @ MetadataAttribute(title="PARALLEL_EXECUTION", role ="EXECUTION_MODE", href = "")
     *      ]
     *      ...
     * )
     */
    public enum EXECUTION_MODE{PARALLEL_EXECUTION}
    public static final String EXECUTION_MODE_NAME = "EXECUTION_MODE";

}
