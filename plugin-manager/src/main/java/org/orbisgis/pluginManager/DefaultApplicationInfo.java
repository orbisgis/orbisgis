package org.orbisgis.pluginManager;

public class DefaultApplicationInfo implements ApplicationInfo {

	private String name;
	private String version;
	private String organization;
	private int wsVersion;

	public DefaultApplicationInfo(String name, String version,
			String organization, int wsVersion) {
		this.name = name;
		this.version = version;
		this.organization = organization;
		this.wsVersion = wsVersion;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getOrganization() {
		return organization;
	}

	public int getWsVersion() {
		return wsVersion;
	}

}
