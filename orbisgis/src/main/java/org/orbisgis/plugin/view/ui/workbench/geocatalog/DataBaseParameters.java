package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.Serializable;

/**
 * This class is responsible for saving the parameters needed to connect to a
 * database (except passwords). It is used in the AddDataBasePanel to remind the
 * user previous connections.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class DataBaseParameters implements Serializable {
	static final long serialVersionUID = 42L;

	private String dbType = null;

	private String host = null;

	private String port = null;

	private String dbName = null;

	private String userName = null;

	public DataBaseParameters(String dbType, String host, String port,
			String dbName, String userName) {
		this.dbType = dbType;
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
	}

	public String getDbType() {
		return dbType;
	}

	public String getHost() {
		return host;
	}

	public String getDbName() {
		return dbName;
	}

	public String getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

}
