/**
 *
 */
package org.orbisgis.geocatalog;

import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;

final class ResourceWizardActionFactory implements IActionFactory {
	/**
	 *
	 */
	private final Catalog catalog;

	/**
	 * @param catalog
	 */
	ResourceWizardActionFactory(Catalog catalog) {
		this.catalog = catalog;
	}

	private final class ResourceWizardAction implements IAction {
		private String wizardId;

		public ResourceWizardAction(Object action) {
			this.wizardId = (String) action;
		}

		public boolean isVisible() {
			return true;
		}

		public boolean isEnabled() {
			return true;
		}

		public void actionPerformed() {
			IResource[] resource = ResourceWizardActionFactory.this.catalog
					.getSelectedResources();
			if (resource.length == 0) {
				EPResourceWizardHelper.runWizard(catalog, wizardId, null);
			} else {
				EPResourceWizardHelper
						.runWizard(catalog, wizardId, resource[0]);
			}
		}
	}

	public IAction getAction(Object action) {
		return new ResourceWizardAction(action);
	}

	public ISelectableAction getSelectableAction(Object action) {
		throw new RuntimeException("bug. Wizard actions are not selectable");
	}
}