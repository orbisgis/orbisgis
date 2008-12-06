package org.gdms.data.wms;

public class WMSSource {

	private String host;
	private String layer;
	private String srs;
	private String format;

	public WMSSource(String host, String layer, String srs, String format) {
		super();
		this.host = host;
		this.layer = layer;
		this.srs = srs;
		this.format = format;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
