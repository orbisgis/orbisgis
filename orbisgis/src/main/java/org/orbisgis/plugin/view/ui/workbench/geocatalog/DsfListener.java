package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.plugin.TempPluginServices;

public class DsfListener implements DataSourceFactoryListener{
	Catalog myCatalog = null;
	
	public void sourceAdded(DataSourceFactoryEvent e) {
		//Later we will also use e.hasDatasource()
		String name = e.getName();
		MyNode node = null;
		String driver = null;
		
		if (e.isWellKnownName()) {
			try {
				driver = TempPluginServices.dsf.getDriver(name);
			} catch (NoSuchTableException e2) {
				e2.printStackTrace();
			}
			if (driver != null) {
				node = new MyNode(name, MyNode.datasource, driver, null);
				myCatalog.addNode(node);
			}
		}
		
	}

	public void sourceNameChanged(DataSourceFactoryEvent e) {
	}

	public void sourceRemoved(DataSourceFactoryEvent e) {
	}

	public void sqlExecuted(DataSourceFactoryEvent event) {
	}
	
	public void setCatalog(Catalog catalog) {
		myCatalog = catalog;
	}

}
