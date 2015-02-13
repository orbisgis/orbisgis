/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.dbjobs.service;

import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.corejdbc.DriverFunctionContainer;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.LinkedList;
import java.util.List;

/**
 * Manage driver functions.
 * @author Nicolas Fortin
 */
public class DriverFunctionContainerImpl implements DriverFunctionContainer {

    private List<DriverFunction> fileDrivers = new LinkedList<>();

    @Override
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addDriverFunction(DriverFunction driverFunction) {
        fileDrivers.add(driverFunction);
    }

    @Override
    public void removeDriverFunction(DriverFunction driverFunction) {
        fileDrivers.remove(driverFunction);
    }

    @Override
    public DriverFunction getImportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
        for(DriverFunction driverFunction : fileDrivers) {
            if(driverFunction.getImportDriverType() == type) {
                for(String fileExt : driverFunction.getImportFormats()) {
                    if(fileExt.equalsIgnoreCase(ext)) {
                        return driverFunction;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public DriverFunction getExportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
        for(DriverFunction driverFunction : fileDrivers) {
            if(driverFunction.getImportDriverType() == type) {
                for(String fileExt : driverFunction.getExportFormats()) {
                    if(fileExt.equalsIgnoreCase(ext)) {
                        return driverFunction;
                    }
                }
            }
        }
        return null;
    }
}
