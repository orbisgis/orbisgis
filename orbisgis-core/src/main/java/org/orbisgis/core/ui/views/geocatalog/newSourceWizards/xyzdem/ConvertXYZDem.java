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
package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.xyzdem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdms.source.SourceManager;
import org.grap.model.GeoProcessorType;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.ui.views.geocatalog.newSourceWizard.INewSource;
import org.orbisgis.core.ui.views.geocatalog.newSourceWizard.SourceRenderer;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.utils.FileUtils;

public class ConvertXYZDem implements INewSource {

	public void registerSources() {

		ConvertXYZDEMWizard convertXYZDEMWizard = new ConvertXYZDEMWizard();
		boolean ok = UIFactory
				.showDialog(convertXYZDEMWizard.getWizardPanels());

		if (ok) {

			File infile = convertXYZDEMWizard.getSelectedInFiles();

			File outfile = convertXYZDEMWizard.getSelectedOutFiles();

			GeoRaster geoRaster;
			try {
				geoRaster = GeoRasterFactory.createGeoRaster(infile
						.getAbsolutePath(), GeoProcessorType.FLOAT,
						convertXYZDEMWizard.getPixelSize());
				geoRaster.open();
				geoRaster.save(outfile.getAbsolutePath());
				DataManager dm = (DataManager) Services
						.getService(DataManager.class);
				SourceManager sourceManager = dm.getSourceManager();
				String name = FileUtils.getFileNameWithoutExtensionU(outfile);
				name = sourceManager.getUniqueName(name);
				sourceManager.register(name, outfile);

			} catch (FileNotFoundException e) {
				Services.getErrorManager().error("File not found", e);
			} catch (IOException e) {
				Services.getErrorManager().error("Cannot convert the file", e);
			}

		}

	}

	public String getName() {

		return "Convert XYZ DEM ";
	}

	@Override
	public SourceRenderer getRenderer() {
		return null;
	}

	@Override
	public void initialize() {
	}

}
