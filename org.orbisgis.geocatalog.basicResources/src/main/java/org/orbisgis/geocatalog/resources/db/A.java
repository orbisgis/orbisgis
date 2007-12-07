package org.orbisgis.geocatalog.resources.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.h2.H2spatialDriver;

public class A {
	public static void main(String[] args) throws DriverException, SQLException {
		final H2spatialDriver dBDriver = new H2spatialDriver();
		final Connection connection = dBDriver.getConnection("", 0,
				"/tmp/h2/essai1", "sa", "");
		for (TableDescription tableDescription : dBDriver.getTables(connection)) {
			System.out.println(tableDescription.getName());
		}
		connection.close();
	}
}
