package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.ReadOnlyDriver;
import org.orbisgis.plugin.TempPluginServices;

import com.hardcode.driverManager.DriverLoadException;

public class DsfListener implements DataSourceFactoryListener{

	Catalog myCatalog = null;
	
	public void sourceAdded(DataSourceFactoryEvent e) {
		String name = e.getName();
		MyNode node = null;
		ReadOnlyDriver driver = null;
		try {
			driver = TempPluginServices.dsf.getDataSource(name).getDriver();
		} catch (DriverLoadException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (NoSuchTableException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (DataSourceCreationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		if (driver != null) {
			node = new MyNode(name, MyNode.datasource,driver.getName(), null);
			myCatalog.addNode(node);
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
