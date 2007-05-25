package org.gdms.data.db;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;



/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class DataBaseTests extends SourceTest {
	/**
	 * Access to the PK field
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	private void testPKAccess(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		d.open();

		String[] pks = d.getDataSourceMetadata().getPrimaryKey();
		assertTrue(pks.length > 0);
		d.cancel();
	}

	public void testPKAccess() throws Exception {
		String[] resources = super.getResourcesWithPK();
		for (String ds : resources) {
			testPKAccess(ds);
		}
	}

	@Override
	protected void setUp() throws Exception {
		setWritingTests(false);
		super.setUp();
	}
}
