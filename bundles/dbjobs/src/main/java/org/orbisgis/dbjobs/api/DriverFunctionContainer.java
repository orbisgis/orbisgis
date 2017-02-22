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
package org.orbisgis.dbjobs.api;

import org.h2gis.api.DriverFunction;

import java.util.List;

/**
 * A class that contains jdbc driver function.
 * @author Nicolas Fortin
 */
public interface DriverFunctionContainer {
    /**
     * @param driverFunction Driver function to add
     */
    void addDriverFunction(DriverFunction driverFunction);

    /**
     * @param driverFunction Driver function to remove
     */
    void removeDriverFunction(DriverFunction driverFunction);

    /**
     * @return List of driver functions
     */
    List<DriverFunction> getDriverFunctionList();

    /**
     * Found DriverFunction using file extension and driver type that export table
     * @param ext Driver extension ex:shp
     * @param type Driver type, copy or link
     * @return Driver instance or null if not found.
     */
    DriverFunction getExportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type );

    /**
     * Found DriverFunction using file extension and driver type that import file
     * @param ext Driver extension ex:shp
     * @param type Driver type, copy or link
     * @return Driver instance or null if not found.
     */
    DriverFunction getImportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type );


    /**
     * The user can load recursively several files from a folder. Popup a GUI to select the folder and execute the link/copy
     * @param dbView View to update
     * @param type Driver type
     */
    void addFilesFromFolder(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type);

    /**
     * The user can load several files from a folder. Popup a GUI to select the folder and execute the link/copy
     * @param dbView View to update
     * @param type Driver type, copy or link
     */
    void importFile(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type);

    /**
     * The user can load recursively several files from a folder. Popup a GUI to select the folder and execute the link/copy
     * @param dbView View to update
     * @param type Driver type
     * @param schema Schema name, null for default schema
     */
    void addFilesFromFolder(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type, String schema);

    /**
     * The user can load several files from a folder. Popup a GUI to select the folder and execute the link/copy
     * @param dbView View to update
     * @param type Driver type, copy or link
     * @param schema Schema name, null for default schema
     */
    void importFile(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type, String schema);
}
