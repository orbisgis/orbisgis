package org.gdms.data.db;

import java.io.Serializable;

public class DBSource implements Serializable {
    private static final long serialVersionUID = 0L;
    private String host;
    private int port;
    private String tableName;
    private String user;
    private String dbName;
    private String password;
	private String prefix;

    public DBSource(String host, int port, String dbName, String user,
    String password, String tableName, String prefix){
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.tableName = tableName;
        this.prefix = prefix;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return host + "-" + port + "-" + dbName + "-" + user + "-" + password + "-" + tableName ;
    }

    public String getDbms() {
        return host + ":" + port + "//" + dbName;
    }

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
