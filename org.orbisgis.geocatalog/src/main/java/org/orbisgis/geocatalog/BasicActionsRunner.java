package org.orbisgis.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.IExtensionRunner;
import org.orbisgis.pluginManager.RegistryFactory;

public class BasicActionsRunner implements IExtensionRunner, ActionListener {

	private Catalog myCatalog;

	public BasicActionsRunner(Catalog myCatalog) {
		this.myCatalog = myCatalog;
		RegistryFactory.getRegistry().registerExtensionRunnerInstance(this);
	}

	public void execute(String param) {
		ExtensionPointManager<IGeocatalogAction> epm = new ExtensionPointManager<IGeocatalogAction>(
				"org.orbisgis.geocatalog.Action");
		IGeocatalogAction action = epm.instantiateFrom(
				"/extension/action[@id='" + param + "']", "class");
		action.actionPerformed(myCatalog);
	}

	public void actionPerformed(ActionEvent e) {
		execute(e.getActionCommand());
	}

}
