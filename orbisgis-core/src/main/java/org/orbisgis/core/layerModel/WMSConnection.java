package org.orbisgis.core.layerModel;

import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;

public class WMSConnection {

	private WMSClient client;
	private WMSStatus status;
	
	public WMSConnection(WMSClient client, WMSStatus status) {
		this.client = client;
		this.status = status;
	}

	public WMSClient getClient() {
		return client;
	}

	public void setClient(WMSClient client) {
		this.client = client;
	}

	public WMSStatus getStatus() {
		return status;
	}

	public void setStatus(WMSStatus status) {
		this.status = status;
	}

}
