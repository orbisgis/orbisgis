package org.orbisgis.core.layerModel;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

import org.gvsig.remoteClient.wms.WMSClient;

public class WMSClientPool {

	private static HashMap<String, WMSClient> clients = new HashMap<String, WMSClient>();

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
