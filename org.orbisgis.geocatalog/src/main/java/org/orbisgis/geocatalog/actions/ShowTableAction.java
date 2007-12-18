package org.orbisgis.geocatalog.actions;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.pluginManager.PluginManager;

public class ShowTableAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return selectedNode.getResourceType() instanceof AbstractGdmsSource;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		new Thread(new Toto(currentNode)).start();
		

	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

	class Toto implements Runnable {
		private IResource currentNode;
		Toto(IResource currentNode) {
			this.currentNode=currentNode;
		}
		public void run() {
			try {
				OrbisgisCore.getDSF().executeSQL(
						"select show ('select * from " + currentNode.getName()
								+ "' , '" + currentNode.getName() + "' ) ");

			} catch (SyntaxException e) {
				throw new RuntimeException("bug", e);
			} catch (DriverLoadException e) {
				throw new RuntimeException("bug", e);
			} catch (NoSuchTableException e) {
				throw new RuntimeException("bug", e);
			} catch (ExecutionException e) {
				PluginManager.error("Cannot show the table", e);
			}

		}
	}
}
