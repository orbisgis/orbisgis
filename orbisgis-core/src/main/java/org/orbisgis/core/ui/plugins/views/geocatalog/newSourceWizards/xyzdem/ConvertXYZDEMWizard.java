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
package org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.xyzdem;

import java.io.File;

import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SIFWizard;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.utils.I18N;

public class ConvertXYZDEMWizard {

	private static final String OUT_FILE_ID = "org.orbisgis.core.ui.geocatalog.XYZConverterOut";

	private static final String IN_FILE_ID = "org.orbisgis.core.ui.geocatalog.XYZConverterIn";

	private OpenFilePanel outfilePanel;

	private UIXYZDEMPanel uixyzPanel;

	private OpenFilePanel infilePanel;

	public UIPanel[] getWizardPanels() {

		infilePanel = new OpenFilePanel(IN_FILE_ID,
				I18N.getText("orbisgis.org.orbisgis.xyz.selectInputFile"));
		infilePanel.addFilter("xyz", "XYZ DEM (*.xyz)");

		uixyzPanel = new UIXYZDEMPanel();

		outfilePanel = new SaveFilePanel(OUT_FILE_ID,
				I18N.getText("orbisgis.org.orbisgis.xyz.selectOutputFileFormat"));
		outfilePanel.addFilter("tif", "TIF with TFW format (*.tif)");

		return new UIPanel[] { infilePanel, uixyzPanel, outfilePanel };
	}

	public File getSelectedInFiles() {
		return infilePanel.getSelectedFile();
	}

	public File getSelectedOutFiles() {
		return outfilePanel.getSelectedFile();
	}

	public static void main(String[] args) {

		// UIFactory.showDialog(new ConvertFileWizard().getWizardPanels());
		SIFWizard sifDialog = UIFactory.getWizard(new ConvertXYZDEMWizard()
				.getWizardPanels());
		sifDialog.pack();
		sifDialog.setVisible(true);

	}

	public float getPixelSize() {
		return uixyzPanel.getPixelSize();
	}
}
