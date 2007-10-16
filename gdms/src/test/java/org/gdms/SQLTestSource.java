/**
 *
 */
package org.gdms;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.file.FileSourceDefinition;

public class SQLTestSource extends TestSource {

	public SQLTestSource(String name, String sql) {
		super(name);
	}

	@Override
	public void backup() throws Exception {
		String name = "test" + System.currentTimeMillis();
		SourceTest.dsf.registerDataSource(name,
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "test.csv")));
		DataSource ret = SourceTest.dsf.executeSQL("select * from " + name);
		SourceTest.dsf.rename(ret.getName(), this.name);
	}

}