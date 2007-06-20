package org.gdms;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;

public class H2TestData extends TestData {

	public static final DataSourceDefinition pointDataSourceDefinition = new DBTableSourceDefinition(
			new DBSource(null, 0, SourceTest.backupDir + File.separator
					+ "h2point", "sa", null, "POINT", "jdbc:h2"));

	private DataSourceDefinition def;

	public H2TestData(String name, int driver, String noPKField,
			boolean hasRepeatedRows, DataSourceDefinition def) {
		super(name, true, driver, H2, true, noPKField, hasRepeatedRows);
		this.def = def;
	}

	@Override
	public String backup(DataSourceFactory dsf) throws Exception {
		try {
			Class.forName("org.h2.Driver");

			Connection c = DriverManager.getConnection("jdbc:h2:"
					+ SourceTest.backupDir + File.separator + "h2point", "sa",
					"");

			Statement st = c.createStatement();

			st.execute("DROP TABLE point IF EXISTS");

			st
					.execute("CREATE TABLE point (id IDENTITY PRIMARY KEY, nom VARCHAR(10), nom2 VARCHAR(100), length DECIMAL(20, 2), area DOUBLE, start DATE, prenom VARCHAR(100), the_geom GEOMETRY)");
			for (int i = 0; i < 4; i++) {
				st
						.execute("INSERT INTO point VALUES("
								+ i
								+ ", 'BOCHER', 'bocher', "
								+ (215.45 + i)
								+ ", 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'))");

			}
			st.close();
			c.close();
		} catch (ClassNotFoundException e) {
			throw new DataSourceCreationException(e);
		} catch (SQLException e) {
			throw new DataSourceCreationException(e);
		}

		String name = "h2" + System.currentTimeMillis();
		dsf.registerDataSource(name, def);
		return name;
	}
}
