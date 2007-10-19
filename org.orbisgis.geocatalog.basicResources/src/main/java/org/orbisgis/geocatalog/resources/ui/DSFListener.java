package org.orbisgis.geocatalog.resources.ui;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.Datasource;

public class DSFListener extends CatalogExtension implements
		DataSourceFactoryListener {

	public DSFListener() {
	}

	public void sourceAdded(DataSourceFactoryEvent e) {
		// Later we will also use e.hasDatasource()
		String name = e.getName();
		String driver = null;

		if (e.isWellKnownName()) {
			try {
				driver = OrbisgisCore.getDSF().getDriver(name);
			} catch (NoSuchTableException e2) {
				e2.printStackTrace();
			}
			if (driver != null) {
				Datasource node = new Datasource(name, driver);
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

	public void initialize() {
	}

}
