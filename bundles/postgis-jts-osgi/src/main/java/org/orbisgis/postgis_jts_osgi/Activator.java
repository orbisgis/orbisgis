/**
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
package org.orbisgis.postgis_jts_osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator
{
	public void start(BundleContext bundleContext) throws Exception
	{
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(DataSourceFactory.OSGI_JDBC_DRIVER_NAME, "PostgreSQL");
		props.put(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION, org.postgresql.Driver.getVersion());
		props.put(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS, org.postgresql.Driver.class.getName());
		serviceRegistration = bundleContext.registerService(DataSourceFactory.class.getName(), new DataSourceFactoryImpl(), props);
	}

	public void stop(BundleContext bundleContext) throws Exception
	{
		if (serviceRegistration != null)
			serviceRegistration.unregister();
	}

	private ServiceRegistration serviceRegistration;
}
