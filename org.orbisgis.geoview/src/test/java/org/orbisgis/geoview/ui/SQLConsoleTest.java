package org.orbisgis.geoview.ui;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.IResource;

public class SQLConsoleTest extends UITest {

	public void testCreateAsSelect() throws Exception {
		openFile("vectorial");
		IResource resource = catalog.getTreeModel().getRoot().getResourceAt(0);
		sqlConsole.setText("select register('/tmp/test.shp', 'temp');"
				+ "\ncreate table temp as select * from " + resource.getName());
		sqlConsole.execute();
		DataSourceFactory dsf = OrbisgisCore.getDSF();
		assertTrue(dsf.exists("temp"));
		DataSource ds = dsf.getDataSource("temp");
		ds.open();
		assertTrue(ds.getRowCount() > 1);
		ds.cancel();

		dsf.getSourceManager().removeAll();
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 0);
		assertTrue(viewContext.getRootLayer().getLayerCount() == 0);
	}
}
