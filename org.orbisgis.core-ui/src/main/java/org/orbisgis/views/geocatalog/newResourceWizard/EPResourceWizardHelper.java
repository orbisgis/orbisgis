/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.geocatalog.newResourceWizard;

import org.orbisgis.resource.IResource;
import org.orbisgis.views.geocatalog.Catalog;
import org.orbisgis.wizards.EPWizardHelper;

public class EPResourceWizardHelper extends
		EPWizardHelper<INewResource, IResource> {

	private Catalog catalog;

	public EPResourceWizardHelper(Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public IResource[] openWizard() {
		catalog.setIgnoreSourceOperations(true);
		IResource[] ret = super.openWizard();
		catalog.setIgnoreSourceOperations(false);

		return ret;
	}

	@Override
	public IResource[] runWizard(String wizardId) {
		catalog.setIgnoreSourceOperations(true);
		IResource[] ret = super.runWizard(wizardId);
		catalog.setIgnoreSourceOperations(false);

		return ret;
	}


	@Override
	protected String getElementName() {
		return "resource";
	}

	@Override
	protected IResource[] getElements(INewResource wizard) {
		return wizard.getResources();
	}

	@Override
	protected String getExtensionPointId() {
		return "org.orbisgis.views.geocatalog.NewResourceWizard";
	}
}
