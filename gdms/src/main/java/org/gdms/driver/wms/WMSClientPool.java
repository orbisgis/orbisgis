/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.gdms.driver.wms;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

import org.gvsig.remoteClient.wms.WMSClient;

/**
 * Pool for all the WMSClient. We can manage easily all the WMSClient
 * 
 */
public class WMSClientPool {

	private static HashMap<String, WMSClient> clients = new HashMap<String, WMSClient>();

        /**
         * Get back the WMSClient.
         * If we never create a connection to the remote server we initialize a new client.
         * the second parameter is at true : getCapabilities(null, true, null)
         * In the other case we return the old WMSClient
         * 
         * @param host
         * @return
         * @throws ConnectException
         * @throws IOException 
         */
	public static WMSClient getWMSClient(String host) throws ConnectException,
			IOException {
		WMSClient client = clients.get(host);
		if (client == null) {
			client = new WMSClient(host);
			client.getCapabilities(null, true, null);
			clients.put(host, client);
			return client;
		}
		return client;
	}
}
