/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.views.geocatalog.newSourceWizard;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.wizards.EPWizardHelper;
import org.orbisgis.core.ui.wizards.WizardAndId;
import org.orbisgis.errorManager.ErrorManager;

public class EPSourceWizardHelper extends EPWizardHelper<INewSource, Boolean> {

	@Override
	public Boolean[] openWizard() {
		Boolean[] ret = super.openWizard();

		return ret;
	}

	@Override
	public Boolean[] runWizard(String wizardId) {
		Boolean[] ret = super.runWizard(wizardId);

		return ret;
	}

	@Override
	protected String getElementName() {
		return "source";
	}

	@Override
	protected Boolean[] getElements(INewSource wizard) {
		wizard.registerSources();
		/*
		 * This wizards perform the addition by themselves. We return a foo
		 * value
		 */
		return new Boolean[0];
	}

	@Override
	protected String getExtensionPointId() {
		return "org.orbisgis.core.ui.views.geocatalog.NewSourceWizard";
	}

	public void initialize() {
		ArrayList<WizardAndId<INewSource>> wizardsAndId = getWizards(null);
		for (WizardAndId<INewSource> wizardAndId : wizardsAndId) {
			try {
				wizardAndId.getWizard().initialize();
			} catch (Exception e) {
				Services.getService(ErrorManager.class).warning(
						"Cannot initialize catalog wizard: "
								+ wizardAndId.getId(), e);
			}
		}
	}

	public SourceRenderer[] getRenderers() {
		ArrayList<SourceRenderer> renderers = new ArrayList<SourceRenderer>();
		ArrayList<WizardAndId<INewSource>> wizardsAndId = getWizards(null);
		for (WizardAndId<INewSource> wizardAndId : wizardsAndId) {
			SourceRenderer renderer = wizardAndId.getWizard().getRenderer();
			if (renderer != null) {
				renderers.add(renderer);
			}
		}

		return renderers.toArray(new SourceRenderer[0]);
	}
}
